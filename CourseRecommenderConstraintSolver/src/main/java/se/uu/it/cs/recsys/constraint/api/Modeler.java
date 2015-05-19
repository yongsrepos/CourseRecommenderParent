package se.uu.it.cs.recsys.constraint.api;

/*
 * #%L
 * CourseRecommenderConstraintSolver
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jacop.core.Store;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.api.type.CourseSchedule;
import se.uu.it.cs.recsys.constraint.constraints.AdvancedCreditsConstraint;
import se.uu.it.cs.recsys.constraint.constraints.AllPeriodsCourseIdUnionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.AvoidSameCourseConstraint;
import se.uu.it.cs.recsys.constraint.constraints.FixedCourseSelectionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.SinglePeriodCourseCardinalityConstraint;
import se.uu.it.cs.recsys.constraint.constraints.SingleStudyPeriodCreditsConstraint;
import se.uu.it.cs.recsys.constraint.constraints.TotalCourseCardinalityConstraint;
import se.uu.it.cs.recsys.constraint.constraints.TotalCreditsConstraint;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.util.CourseFlattener;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class Modeler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Modeler.class);

    private final Set<Integer> interestedCourseIdSet = new HashSet<>();

    private final Set<Course> interestedCourses = new HashSet<>();

    private final Map<CourseLevel, Set<Integer>> levelToInterestedCourseIds
            = new HashMap<>();

    private final Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds
            = new HashMap<>();

    private final Map<Integer, Set<Integer>> periodNumToMustHaveIdSet
            = new HashMap<>();

    private final List<Set<Integer>> sameCourseWithDiffIdInPlanYear
            = new ArrayList<>();

    private final Set<CourseSchedule> schedulePeriodsInfo = new HashSet<>();

    @Autowired
    private CourseRepository courseRepository;

    public void postConstraints(Store store, SetVar[] pers,
            Set<Integer> argInterestedCourseIdSet) {

        initFields(argInterestedCourseIdSet);

        //1. on cardinality
        SinglePeriodCourseCardinalityConstraint.impose(store, pers);
        TotalCourseCardinalityConstraint.impose(store, pers);

        //2. on the must-have ones
        FixedCourseSelectionConstraint.impose(store, pers, periodNumToMustHaveIdSet);

        SetVar allCourseIdUnion = AllPeriodsCourseIdUnionConstraint.imposeAndGetUnion(store, pers, this.interestedCourseIdSet);

        //3. avoid selecting same course
        AvoidSameCourseConstraint.impose(store, allCourseIdUnion, this.sameCourseWithDiffIdInPlanYear);

        //4. on credits
        AdvancedCreditsConstraint.impose(store,
                allCourseIdUnion,
                getCreditToInterestedAdvancedCourseIdSetMapping(),
                AdvancedCreditsConstraintConfig.getInstanceWithDefaultValues());

        SingleStudyPeriodCreditsConstraint.impose(store, pers, creditToInterestedCourseIds);

        TotalCreditsConstraint.impose(store,
                allCourseIdUnion,
                this.creditToInterestedCourseIds,
                TotalCreditsConstraintConfig.getInstanceWithDefaultValues());

    }

    public void setScheduleInfo(Set<CourseSchedule> schedulePeriodsInfo) {
        if (schedulePeriodsInfo == null || schedulePeriodsInfo.isEmpty()) {
            throw new IllegalArgumentException("Schedule info can not be empty!");
        }

        this.schedulePeriodsInfo.addAll(schedulePeriodsInfo);
    }

    public void addMandatoryPlan(Map<Integer, Set<Integer>> periodOneBasedIdxToMustHaveIdSet) {
        if (periodOneBasedIdxToMustHaveIdSet == null || periodOneBasedIdxToMustHaveIdSet.isEmpty()) {
            LOGGER.info("No mandatory selection set.");
            return;
        }

        this.periodNumToMustHaveIdSet.putAll(periodOneBasedIdxToMustHaveIdSet);
    }

    public void addMandatoryCourse(Set<se.uu.it.cs.recsys.api.type.Course> mandatoryCourseSet) {
        if (mandatoryCourseSet == null || mandatoryCourseSet.isEmpty()) {
            LOGGER.info("No mandatory selection set.");
            return;
        }

        mandatoryCourseSet.forEach(course -> {
            Course courseEntity = this.courseRepository
                    .findByCodeAndTaughtYearAndStartPeriod(
                            course.getCode(),
                            course.getTaughtYear().shortValue(),
                            course.getStartPeriod().shortValue());

            int periodIdx = findSchedulePeriodIndex(course.getTaughtYear().shortValue(),
                    course.getStartPeriod().shortValue());

            if (this.periodNumToMustHaveIdSet.containsKey(periodIdx)) {
                this.periodNumToMustHaveIdSet.get(periodIdx).add(courseEntity.getAutoGenId());
            } else {
                this.periodNumToMustHaveIdSet.put(periodIdx,
                        Stream.of(courseEntity.getAutoGenId()).collect(Collectors.toSet()));
            }
        });
    }

    private Map<CourseCredit, Set<Integer>> getCreditToInterestedAdvancedCourseIdSetMapping() {

        Set<Course> interestedAdvancedCourseSet = this.interestedCourses.stream()
                .filter(course -> course.getLevel().getLevel()
                        .equals(CourseLevel.ADVANCED.getDBString()))
                .collect(Collectors.toSet());

        return CourseFlattener.flattenToCreditAndIdSetMap(interestedAdvancedCourseSet)
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    return CourseCredit.ofValue(entry.getKey().getCredit().floatValue());
                }, Map.Entry::getValue));
    }

    private void initFields(Set<Integer> argInterestedCourseIdSet) {
        //1. init interested id set
        this.interestedCourseIdSet.addAll(argInterestedCourseIdSet);

        //2. init interest course set
        this.interestedCourses.addAll(this.courseRepository.findByAutoGenIds(this.interestedCourseIdSet));

        //3. categorize course set based on credit
        CourseFlattener.flattenToCreditAndIdSetMap(this.interestedCourses)
                .entrySet().stream()
                .forEach(entry -> {
                    this.creditToInterestedCourseIds.put(
                            CourseCredit.ofValue(entry.getKey().getCredit().floatValue()),
                            entry.getValue());
                });

        //4. categorize course set based on level
        CourseFlattener.flattenToLevelAndIdSetMap(this.interestedCourses)
                .entrySet().stream()
                .forEach(entry -> {
                    this.levelToInterestedCourseIds.put(
                            CourseLevel.ofDBString(entry.getKey().getLevel()),
                            entry.getValue());
                });

        //6. set same course with diff codes
        initSameCourseWithDiffIdInPlanYear();
    }

    private void initSameCourseWithDiffIdInPlanYear() {
        this.sameCourseWithDiffIdInPlanYear.addAll(getSameCourseWithDiffCodeInPlanYear());

        List<Course> allCourses = this.courseRepository.findAll();

        int firstPlanYear = this.schedulePeriodsInfo
                .stream()
                .sorted((schedule1, schedule2)
                        -> Short.compare(
                                schedule1.getTaughtYear(),
                                schedule2.getTaughtYear()))
                .findFirst()
                .get()
                .getTaughtYear();

        Set<Course> planYearCourseSet = allCourses.stream()
                .filter(course -> course.getTaughtYear() >= firstPlanYear)
                .collect(Collectors.toSet());

        Map<String, Set<Integer>> codeToIdSet = CourseFlattener
                .flattenToCodeAndIdSet(planYearCourseSet);

        codeToIdSet.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry
                        -> {
                    LOGGER.debug("code: {}, idSet {}", entry.getKey(), entry.getValue());
                    this.sameCourseWithDiffIdInPlanYear.add(entry.getValue());
                });
    }

    private List<Set<Integer>> getSameCourseWithDiffCodeInPlanYear() {
        // for example, course 1DL301 and 1DL300, both are Database Design I
        Set<Integer> dl300IdSet = this.courseRepository
                .findByCode("1DL300")
                .stream()
                .filter(course -> course.getTaughtYear() >= 2015)
                .flatMap(course -> Stream.of(course.getAutoGenId()))
                .collect(Collectors.toSet());

        Set<Integer> dl301IdSet = this.courseRepository
                .findByCode("1DL301")
                .stream()
                .filter(course -> course.getTaughtYear() >= 2015)
                .flatMap(course -> Stream.of(course.getAutoGenId()))
                .collect(Collectors.toSet());

        dl300IdSet.addAll(dl301IdSet);

        return Stream.of(dl300IdSet).collect(Collectors.toList());
    }

    private int findSchedulePeriodIndex(short taughtYear, short startPeriod) {
        for (CourseSchedule schedule : this.schedulePeriodsInfo) {
            if (schedule.getTaughtYear() == taughtYear && schedule.getStartPeriod() == startPeriod) {
                return schedule.getPeriodIdxAmongAllPlanPeriods();
            }
        }

        throw new IllegalStateException("Has not schedule info for taught year "
                + taughtYear + ", start period " + startPeriod);
    }
}
