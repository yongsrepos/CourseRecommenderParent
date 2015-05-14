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
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 */
@Component
public class LevelDomainBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LevelDomainBuilder.class);

    @Autowired
    private CourseRepository courseRepository;

    public Map<CourseLevel, SetDomain> getLevelIdConstaintDomainMappingFor(
            Set<Integer> idSet) {

        Map<CourseLevel, SetDomain> levelAndDomain = new HashMap<>();

        Map<CourseLevel, Set<Integer>> levelAndIds = getLevelAndIdMappingFor(idSet);

        levelAndIds.entrySet().stream().forEach((entry) -> {
            levelAndDomain.put(entry.getKey(),
                    DomainBuilder.createDomain(entry.getValue()));
        });

        return levelAndDomain;
    }

    public Map<CourseLevel, SetDomain> getLevelAndIdConstaintDomain() {
        Map<CourseLevel, SetDomain> levelAndDomain = new HashMap<>();

        Map<CourseLevel, Set<Integer>> levelAndIds = getLevelAndIds();

        levelAndIds.entrySet().stream().forEach((entry) -> {
            levelAndDomain.put(entry.getKey(),
                    DomainBuilder.createDomain(entry.getValue()));
        });

        return levelAndDomain;
    }

    public Map<CourseLevel, Set<Integer>> getLevelAndIds() {

        Map<CourseLevel, Set<Integer>> levelAndIds = new HashMap<>();

        this.courseRepository.findAll()
                .stream()
                .forEach(course -> checkCourseLevelAndPutToMap(course, levelAndIds));

        return levelAndIds;
    }

    public Map<CourseLevel, Set<Integer>> getLevelAndIdMappingFor(Set<Integer> idSet) {
        if (idSet == null || idSet.isEmpty()) {
            LOGGER.warn("Does not make sense to put null or empty set, right?");
            return Collections.EMPTY_MAP;
        }
        
        Map<CourseLevel, Set<Integer>> levelAndIds = new HashMap<>();

        this.courseRepository.findByAutoGenIds(idSet)
                .stream()
                .forEach(course -> checkCourseLevelAndPutToMap(course, levelAndIds));

        return levelAndIds;
    }

    /*
     * Put course to the level to course map if its credit is supported; otherwise log it as warning
     */
    private void checkCourseLevelAndPutToMap(Course course, Map<CourseLevel, Set<Integer>> creditAndIds) {
        try {
            CourseLevel courseLevel = CourseLevel.ofDBString(course.getLevel().getLevel());

            if (creditAndIds.containsKey(courseLevel)) {
                creditAndIds.get(courseLevel).add(course.getAutoGenId());
            } else {
                Set<Integer> idSetWithSameLevel = new HashSet<>();
                idSetWithSameLevel.add(course.getAutoGenId());

                creditAndIds.put(courseLevel, idSetWithSameLevel);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Course: {} id: {} has a unsupported course level {}",
                    course.getName(), course.getAutoGenId(), course.getLevel().getLevel(), e);

        }
    }

}
