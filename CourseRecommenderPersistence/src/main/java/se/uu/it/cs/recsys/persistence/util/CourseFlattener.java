package se.uu.it.cs.recsys.persistence.util;

/*
 * #%L
 * CourseRecommenderPersistence
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


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;

/**
 *
 * @author Yong Huang <yong.e.huang@gmail.com>
 */
public class CourseFlattener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseFlattener.class);

    public static Map<SupportedCourseLevel, Course> flattenBasedOnLevel(Set<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_MAP;
        }

        return courses.stream()
                .collect(Collectors
                        .toMap(Course::getLevel, Function.identity()));
    }

    public static Map<SupportedCourseCredit, Course> flattenBasedOnCredit(Set<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_MAP;
        }

        return courses.stream()
                .collect(Collectors
                        .toMap(Course::getCredit, Function.identity()));

    }

    public static Set<Integer> flattenToIdSet(Set<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_SET;
        }

        return courses.stream()
                .map(course -> course.getAutoGenId())
                .collect(Collectors.toSet());
    }

}
