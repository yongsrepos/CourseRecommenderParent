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


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseSchedule;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.util.CourseFlattener;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class SameCourseFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SameCourseFinder.class);

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Gets the info about course id collection representing the same course.
     * E.g a course with code "1DL301" can be planned in year 2015 and 2016 and
     * it will have different ids in DB in this case.
     *
     * @param scheduleInfo the info regarding which years are planned, e.g 2015;
     * non-empty input.
     * @return non-null collection regarding id set for same course.
     * @throws IllegalArgumentException if input is null or empty
     */
    public Set<Set<Integer>> getIdCollectionForSameCourse(Set<CourseSchedule> scheduleInfo) {

        if (scheduleInfo == null || scheduleInfo.isEmpty()) {
            throw new IllegalArgumentException("Schedule input must be non-empty");
        }

        List<Course> allCourses = this.courseRepository.findAll();

        // find the first plan year, e.g 2015
        int firstPlanYear = scheduleInfo
                .stream()
                .sorted((schedule1, schedule2)
                        -> Short.compare(
                                schedule1.getTaughtYear(),
                                schedule2.getTaughtYear()))
                .findFirst()
                .get()
                .getTaughtYear();

        // only get the course ids for plan year, e.g 2015, 2016
        Set<Course> planYearCourseSet = allCourses.stream()
                .filter(course -> course.getTaughtYear() >= firstPlanYear)
                .collect(Collectors.toSet());

        Map<String, Set<Integer>> codeToIdSet = CourseFlattener
                .flattenToCodeAndIdSet(planYearCourseSet);

        Set<Set<Integer>> idCollectionForSameCourse = new HashSet<>();

        codeToIdSet.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry
                        -> {
                    idCollectionForSameCourse.add(entry.getValue());
                });

        return idCollectionForSameCourse;
    }

    /**
     * For example, course 1DL301 and 1DL300, both are Database Design I, but
     * planned in different periods.
     *
     * @param courseCodeSet, the course collection that at most only one from it can
     * be selected.
     * @param firstTaughtYear, the first year that the course is taught in the plan years
     * @return non-null collection of the course id
     */
    public Set<Integer> getIdCollectionToAvoidMoreThanOneSel(Set<String> courseCodeSet, Integer firstTaughtYear) {

        Set<Integer> totalIdSet = new HashSet<>();

//        Short firstTaughtYear = null;
//
//        for (se.uu.it.cs.recsys.api.type.Course c : courseSet) {
//            if (firstTaughtYear == null) {
//                firstTaughtYear = c.getTaughtYear().shortValue();
//            } else {
//                if (firstTaughtYear > c.getTaughtYear()) {
//                    firstTaughtYear = c.getTaughtYear().shortValue();
//                }
//            }
//        }

//        final short finalFirstTaughtYear = firstTaughtYear;

        courseCodeSet.forEach(code -> {
            Set<Integer> partSet
                    = this.courseRepository
                    .findByCode(code)
                    .stream()
                    .filter(entity -> entity.getTaughtYear() >= firstTaughtYear)
                    .map(entity -> entity.getAutoGenId())
                    .collect(Collectors.toSet());

            if (!partSet.isEmpty()) {
                totalIdSet.addAll(partSet);
            }
        });

        return totalIdSet;
    }

}
