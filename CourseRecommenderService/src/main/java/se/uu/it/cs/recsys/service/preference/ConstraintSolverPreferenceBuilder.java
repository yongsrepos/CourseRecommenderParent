package se.uu.it.cs.recsys.service.preference;

/*
 * #%L
 * CourseRecommenderService
 * %%
 * Copyright (C) 2015 Yong Huang  <yong.e.huang@gmail.com >
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseSchedule;
import se.uu.it.cs.recsys.api.type.CourseSelectionPreference;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class ConstraintSolverPreferenceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintSolverPreferenceBuilder.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SameCourseFinder sameCourseFinder;

    CourseSelectionPreference apiPref;

    public ConstraintSolverPreference build(CourseSelectionPreference pref) {
        if (pref == null) {
            throw new IllegalArgumentException("Preferernce can not be null!");
        }

        this.apiPref = pref;

        ConstraintSolverPreference instance = buildMandatoryFields();

        Set<CourseSchedule> scheduleInfo = instance.getIndexedScheduleInfo();

        setAvoidMoreThanOneSelCollection(instance, scheduleInfo);

        setMaxAdvancedCredits(instance);
        setMaxCourseAmount(instance);
        setMaxTotalCredits(instance);
        setRecommendationAmount(instance);

        setPeriodIdxToMustTakeCourseId(instance, scheduleInfo);

        return instance;
    }

    private ConstraintSolverPreference buildMandatoryFields() {

        Set<CourseSchedule> scheduleInfo = getIndexedScheduleInfo();

        Set<Integer> interestedIdSet = getInterestedCourseIdCollection(scheduleInfo);

        ConstraintSolverPreference instance = new ConstraintSolverPreference
                .Builder()
                .setInterestedCourseIdCollection(interestedIdSet)
                .setIndexedScheduleInfo(scheduleInfo)
                .build();

        return instance;

    }

    private void setAvoidMoreThanOneSelCollection(ConstraintSolverPreference instance,
            Set<CourseSchedule> scheduleInfo) {

        Set<Set<Integer>> totalAvoidMoreThanOneSelCollection
                = this.sameCourseFinder.getIdCollectionForSameCourse(scheduleInfo);

        Set<Set<String>> avoidMoreThanOneSelFromSameCourseSet = this.apiPref
                .getAvoidMoreThanOneFromTheSameCollection();

        short firstPlanYear = getFirstPlanYear(scheduleInfo);

        if (avoidMoreThanOneSelFromSameCourseSet != null
                && !avoidMoreThanOneSelFromSameCourseSet.isEmpty()) {

            avoidMoreThanOneSelFromSameCourseSet.forEach(singleSet -> {

                LOGGER.debug("At most one from the collection can be selected: {}",
                        singleSet);

                Set<Integer> partialAvoidSet = this.sameCourseFinder
                        .getIdCollectionToAvoidMoreThanOneSel(
                                singleSet,
                                (int) firstPlanYear);

                totalAvoidMoreThanOneSelCollection.add(partialAvoidSet);
            });

        }

        if (!totalAvoidMoreThanOneSelCollection.isEmpty()) {
            instance.getAvoidCollectionForCourseWithDiffIdSet()
                    .addAll(totalAvoidMoreThanOneSelCollection);
        }

    }

    private static short getFirstPlanYear(Set<CourseSchedule> scheduleInfo) {
        return scheduleInfo
                .stream()
                .sorted((s1, s2) -> Short.compare(s1.getTaughtYear(), s2.getTaughtYear()))
                .findFirst()
                .get()
                .getTaughtYear();
    }

    private Set<CourseSchedule> getIndexedScheduleInfo() {

        Set<CourseSchedule> apiScheduleInfo = this.apiPref.getIndexedScheduleInfo();

        if (apiScheduleInfo == null || apiScheduleInfo.isEmpty()) {
            return getDefaultIndexedScheduleInfo();
        } else {
            return apiScheduleInfo;
        }

    }

    private Set<Integer> getInterestedCourseIdCollection(Set<CourseSchedule> scheduleInfo) {

        Set<String> codes = this.apiPref.getInterestedCourseCodeCollection();

        final Set<Integer> totalIdSet = new HashSet<>();

        if (codes == null || codes.isEmpty()) {

            totalIdSet.addAll(this.courseRepository.findAll()
                    .stream()
                    .filter(inPlanYearPredicate(scheduleInfo))
                    .map(c -> c.getAutoGenId())
                    .collect(Collectors.toSet())
            );

        } else {
            codes.forEach(code -> {
                Set<Integer> partIds = this.courseRepository
                        .findByCode(code)
                        .stream()
                        .filter(inPlanYearPredicate(scheduleInfo))
                        .map(c -> c.getAutoGenId())
                        .collect(Collectors.toSet());

                if (!partIds.isEmpty()) {
                    totalIdSet.addAll(partIds);
                }
            });
        }

        return totalIdSet;
    }

    public static Predicate<Course> inPlanYearPredicate(Set<CourseSchedule> scheduleInfo) {
        return course -> inPlanYear(course, scheduleInfo);
    }

    private static boolean inPlanYear(Course course, Set<CourseSchedule> scheduleInfo) {
        Set<Boolean> bingo = new HashSet<>();

        scheduleInfo.forEach(schedule -> {
            if (Short.compare(course.getTaughtYear(), schedule.getTaughtYear()) == 0
                    && Short.compare(course.getStartPeriod(), schedule.getStartPeriod()) == 0) {
                bingo.add(Boolean.TRUE);
            }
        });

        return !bingo.isEmpty();

    }

    private void setMaxAdvancedCredits(ConstraintSolverPreference instance) {

        if (this.apiPref.getMaxAdvancedCredits() == null) {
            instance.setMaxAdvancedCredits(ConstraintSolverPreference.MAX_ADVANCED_CREDIT_DEFAULT);
        } else {
            instance.setMaxAdvancedCredits(this.apiPref.getMaxAdvancedCredits());
        }

    }

    private void setMaxCourseAmount(ConstraintSolverPreference instance) {

        int maxCourseAmount = this.apiPref.getMaxCourseAmount() != null
                ? this.apiPref.getMaxCourseAmount()
                : ConstraintSolverPreference.MAX_TOTAL_COURSE_AMOUNT_DEFAULT;

        instance.setMaxCourseAmount(maxCourseAmount);

    }

    private void setMaxTotalCredits(ConstraintSolverPreference instance) {

        Double maxTotalCredit = this.apiPref.getMaxTotalCredits() != null
                ? this.apiPref.getMaxTotalCredits()
                : ConstraintSolverPreference.MAX_TOTAL_CREDIT_DEFAULT;

        instance.setMaxTotalCredits(maxTotalCredit);

    }

    private void setPeriodIdxToMustTakeCourseId(ConstraintSolverPreference instance,
            Set<CourseSchedule> scheduleInfo) {

        if (this.apiPref.getMustTakeCourseCodeCollection() == null
                || this.apiPref.getMustTakeCourseCodeCollection().isEmpty()) {
            LOGGER.info("No mandatory selection set.");
            return;
        }

        Map<Integer, Set<Integer>> periodIdxToMustHaveCourseId = new HashMap<>();

        this.apiPref.getMustTakeCourseCodeCollection().forEach(course -> {
            Course courseEntity = this.courseRepository
                    .findByCodeAndTaughtYearAndStartPeriod(
                            course.getCode(),
                            course.getTaughtYear().shortValue(),
                            course.getStartPeriod().shortValue());

            int periodIdx = findSchedulePeriodIndex(
                    course.getTaughtYear().shortValue(),
                    course.getStartPeriod().shortValue(),
                    scheduleInfo);

            if (periodIdxToMustHaveCourseId.containsKey(periodIdx)) {
                periodIdxToMustHaveCourseId.get(periodIdx).add(courseEntity.getAutoGenId());
            } else {
                periodIdxToMustHaveCourseId.put(periodIdx,
                        Stream.of(courseEntity.getAutoGenId())
                        .collect(Collectors.toSet()));
            }
        });

        if (!periodIdxToMustHaveCourseId.isEmpty()) {
            instance.getPeriodIdxToMustTakeCourseId()
                    .putAll(periodIdxToMustHaveCourseId);
        }
    }

    private void setRecommendationAmount(ConstraintSolverPreference instance) {

        int recommendatonAmount = this.apiPref.getRecommendationAmount() != null
                ? this.apiPref.getRecommendationAmount()
                : ConstraintSolverPreference.RECOMMENDATION_AMOUNT_DEFAULT;

        instance.setRecommendationAmount(recommendatonAmount);

    }

    private int findSchedulePeriodIndex(short taughtYear, short startPeriod,
            Set<CourseSchedule> scheduleInfo) {

        for (CourseSchedule schedule : scheduleInfo) {
            if (schedule.getTaughtYear() == taughtYear
                    && schedule.getStartPeriod() == startPeriod) {
                return schedule.getPeriodIdxAmongAllPlanPeriods();
            }
        }

        throw new IllegalStateException("Has no schedule info for taught year "
                + taughtYear + ", start period " + startPeriod);
    }

    public static Set<CourseSchedule> getDefaultIndexedScheduleInfo() {
        return Stream.of(new CourseSchedule((short) 2015, (short) 1, (short) 1),
                new CourseSchedule((short) 2015, (short) 2, (short) 2),
                new CourseSchedule((short) 2016, (short) 3, (short) 3),
                new CourseSchedule((short) 2016, (short) 4, (short) 4),
                new CourseSchedule((short) 2016, (short) 1, (short) 5),
                new CourseSchedule((short) 2016, (short) 2, (short) 6))
                .collect(Collectors.toSet());
    }

}
