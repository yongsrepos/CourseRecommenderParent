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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jacop.set.core.SetDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 */
@Component
public class CreditDomainBuilder {

    @Autowired
    private CourseRepository courseRepository;

    public Map<CourseCredit, SetDomain> getCreditAndIdConstaintDomainMappingFor(
            Set<Integer> idSet) {

        Map<CourseCredit, SetDomain> creditAndDomain = new HashMap<>();

        Map<CourseCredit, Set<Integer>> levelAndIds = getCreditAndIdMappingFor(idSet);

        levelAndIds.entrySet().stream().forEach((entry) -> {
            creditAndDomain.put(entry.getKey(),
                    DomainBuilder.createSetDomain(entry.getValue()));
        });

        return creditAndDomain;
    }

    public Map<CourseCredit, SetDomain> getCreditAndIdConstaintDomain() {
        Map<CourseCredit, SetDomain> creditAndDomain = new HashMap<>();

        Map<CourseCredit, Set<Integer>> creditAndIds = getCreditAndIds();

        creditAndIds.entrySet().stream()
                .forEach((entry) -> {
                    creditAndDomain.put(entry.getKey(),
                            DomainBuilder.createSetDomain(entry.getValue()));
                });

        return creditAndDomain;
    }

    public Map<CourseCredit, Set<Integer>> getCreditAndIds() {
        Map<CourseCredit, Set<Integer>> levelAndIds = new HashMap<>();

        for (CourseCredit credit : CourseCredit.values()) {

            SupportedCourseCredit supportedCourseCredit
                    = new SupportedCourseCredit((double) credit.getCredit());

            // in DB, course has 7.5 pts
            if (credit.equals(CourseCredit.SEVEN)) {
                supportedCourseCredit = new SupportedCourseCredit(7.5);
            }

            Set<Course> courseSet = this.courseRepository
                    .findByCredit(supportedCourseCredit);

            Set<Integer> idSet = courseSet.stream()
                    .map(course -> course.getAutoGenId())
                    .collect(Collectors.toSet());

            levelAndIds.put(credit, idSet);
        }

        return levelAndIds;
    }

    public Map<CourseCredit, Set<Integer>> getCreditAndIdMappingFor(Set<Integer> idSet) {
        Map<CourseCredit, Set<Integer>> creditAndIds = new HashMap<>();

        idSet.forEach(id -> {
            Course course = this.courseRepository.findByAutoGenId(id);

            // takes credit 7.5 as 7.0
            Integer creditIntValue = course.getCredit().getCredit().intValue();
            CourseCredit credit = CourseCredit.ofValue(creditIntValue);

            if (creditAndIds.containsKey(credit)) {
                creditAndIds.get(credit).add(course.getAutoGenId());
            } else {
                Set<Integer> idSetWithSameCredit = new HashSet<>();
                idSetWithSameCredit.add(course.getAutoGenId());

                creditAndIds.put(credit, idSetWithSameCredit);
            }
        });

        return creditAndIds;
    }

}
