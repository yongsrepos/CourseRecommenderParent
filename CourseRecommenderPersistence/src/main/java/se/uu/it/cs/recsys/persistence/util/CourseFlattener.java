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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseFlattener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseFlattener.class);

    public static Map<SupportedCourseLevel, Set<Integer>> flattenToLevelAndIdSetMap(Set<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_MAP;
        }

        Map<SupportedCourseLevel, Set<Integer>> levelAndIdSetMapping = new HashMap<>();

        courses.forEach(course -> {
            if (levelAndIdSetMapping.containsKey(course.getLevel())) {
                levelAndIdSetMapping.get(course.getLevel()).add(course.getAutoGenId());
            } else {
                Set<Integer> idSet = Stream.of(course.getAutoGenId()).collect(Collectors.toSet());
                levelAndIdSetMapping.put(course.getLevel(), idSet);
            }
        });

        return levelAndIdSetMapping;
    }

    public static Map<SupportedCourseCredit, Set<Integer>> flattenToCreditAndIdSetMap(Set<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_MAP;
        }

        Map<SupportedCourseCredit, Set<Integer>> creditAndIdSetMapping = new HashMap<>();

        courses.forEach(course -> {
            if (creditAndIdSetMapping.containsKey(course.getCredit())) {
                creditAndIdSetMapping.get(course.getCredit()).add(course.getAutoGenId());
            } else {
                Set<Integer> idSet = Stream.of(course.getAutoGenId()).collect(Collectors.toSet());
                creditAndIdSetMapping.put(course.getCredit(), idSet);
            }
        });

        return creditAndIdSetMapping;

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

    public static Map<String, Set<Integer>> flattenToCodeAndIdSet(Set<Course> courseSet) {
        if (courseSet == null || courseSet.isEmpty()) {
            LOGGER.warn("Does not make send to flatten a null or empty collection, right?");

            return Collections.EMPTY_MAP;
        }

        Map<String, Set<Integer>> codeAndIdSet = new HashMap<>();

        courseSet.forEach(course -> {
            String code = course.getCode();

            if (codeAndIdSet.containsKey(code)) {
                codeAndIdSet.get(code).add(course.getAutoGenId());
            } else {
                codeAndIdSet.put(code,
                        Stream.of(course.getAutoGenId())
                        .collect(Collectors.toSet()));
            }
        });

        return codeAndIdSet;
    }

}
