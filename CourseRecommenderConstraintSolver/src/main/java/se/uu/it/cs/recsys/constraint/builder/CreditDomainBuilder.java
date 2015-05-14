/* 
 * Copyright 2015 Yong Huang <yong.e.huang@gmail.com>.
 *
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
 */
package se.uu.it.cs.recsys.constraint.builder;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jacop.set.core.SetDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 * A domain builder for normalized credit. Since course credit can have float
 * value, e.g 7.5f, the class will then normalize it to have credit (int) 7.5 *
 * 10 instead. Hence, we can model the CSP with IntVar and int constraint.
 */
@Component
public class CreditDomainBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditDomainBuilder.class);

    @Autowired
    private CourseRepository courseRepository;

    /**
     *
     * @param idSet
     * @return the mapping between credit and the constraint set domain for the
     * input courses ids from planned years
     */
    public Map<CourseCredit, SetDomain> getCreditAndIdConstaintDomainMappingFor(
            Set<Integer> idSet) {
        if (idSet == null || idSet.isEmpty()) {
            LOGGER.warn("Does not make sense to put null or empty set, right?");
            return Collections.EMPTY_MAP;
        }

        Map<CourseCredit, SetDomain> creditAndDomain = new HashMap<>();

        Map<CourseCredit, Set<Integer>> levelAndIds = getCreditAndIdMappingFor(idSet);

        levelAndIds.entrySet().stream().forEach((entry) -> {
            creditAndDomain.put(entry.getKey(),
                    DomainBuilder.createDomain(entry.getValue()));
        });

        return creditAndDomain;
    }

    /**
     *
     * @return the mapping between credit and the constraint set domain for all
     * courses ids from planned years
     */
    public Map<CourseCredit, SetDomain> getCreditAndCourseIdConstaintDomain() {
        Map<CourseCredit, SetDomain> creditAndDomain = new HashMap<>();

        Map<CourseCredit, Set<Integer>> creditAndIds = getCreditAndIds();

        creditAndIds.entrySet().stream()
                .forEach((entry) -> {
                    creditAndDomain.put(entry.getKey(),
                            DomainBuilder.createDomain(entry.getValue()));
                });

        return creditAndDomain;
    }

    /**
     *
     * @return the mapping between credit and all courses ids from planned years
     */
    public Map<CourseCredit, Set<Integer>> getCreditAndIds() {
        Map<CourseCredit, Set<Integer>> creditAndIds = new HashMap<>();

        this.courseRepository.findAll().forEach(course -> {
            checkCourseCreditAndPutToMap(course, creditAndIds);
        });

        return creditAndIds;
    }

    /**
     *
     * @param idSet the set of course ids
     * @return mapping between course credit and course
     */
    public Map<CourseCredit, Set<Integer>> getCreditAndIdMappingFor(Set<Integer> idSet) {
        if (idSet == null || idSet.isEmpty()) {
            LOGGER.warn("Does not make sense to put null or empty set, right?");
            return Collections.EMPTY_MAP;
        }

        Map<CourseCredit, Set<Integer>> creditAndIds = new HashMap<>();

        this.courseRepository.findByAutoGenIds(idSet).forEach(course -> {
            checkCourseCreditAndPutToMap(course, creditAndIds);
        });

        return creditAndIds;
    }

    /*
     * Put course to the credit to course map if its credit is supported; otherwise log it as warning
     */
    private void checkCourseCreditAndPutToMap(Course course, Map<CourseCredit, Set<Integer>> creditAndIds) {
        try {
            CourseCredit courseCredit = CourseCredit.ofValue(course.getCredit().getCredit().floatValue());

            if (creditAndIds.containsKey(courseCredit)) {
                creditAndIds.get(courseCredit).add(course.getAutoGenId());
            } else {
                Set<Integer> idSetWithSameCredit = new HashSet<>();
                idSetWithSameCredit.add(course.getAutoGenId());

                creditAndIds.put(courseCredit, idSetWithSameCredit);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Course: {} id: {} has a unsupported course credit {}",
                    course.getName(), course.getAutoGenId(), course.getCredit().getCredit(), e);

        }
    }

}
