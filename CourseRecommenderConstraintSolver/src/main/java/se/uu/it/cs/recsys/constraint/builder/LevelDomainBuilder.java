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
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 */
@Component
public class LevelDomainBuilder {

    @Autowired
    private CourseRepository courseRepository;

    public Map<CourseLevel, SetDomain> getLevelIdConstaintDomainMappingFor(
            Set<Integer> idSet) {

        Map<CourseLevel, SetDomain> levelAndDomain = new HashMap<>();

        Map<CourseLevel, Set<Integer>> levelAndIds = getLevelAndIdMappingFor(idSet);

        levelAndIds.entrySet().stream().forEach((entry) -> {
            levelAndDomain.put(entry.getKey(),
                    DomainBuilder.createSetDomain(entry.getValue()));
        });

        return levelAndDomain;
    }

    public Map<CourseLevel, SetDomain> getLevelAndIdConstaintDomain() {
        Map<CourseLevel, SetDomain> levelAndDomain = new HashMap<>();

        Map<CourseLevel, Set<Integer>> levelAndIds = getLevelAndIds();

        levelAndIds.entrySet().stream().forEach((entry) -> {
            levelAndDomain.put(entry.getKey(),
                    DomainBuilder.createSetDomain(entry.getValue()));
        });

        return levelAndDomain;
    }

    public Map<CourseLevel, Set<Integer>> getLevelAndIds() {
        Map<CourseLevel, Set<Integer>> levelAndIds = new HashMap<>();

        for (CourseLevel level : CourseLevel.values()) {
            Set<Course> courseSet = this.courseRepository
                    .findByLevel(new SupportedCourseLevel(level.name()));

            Set<Integer> idSet = courseSet.stream()
                    .map(course -> course.getAutoGenId())
                    .collect(Collectors.toSet());

            levelAndIds.put(level, idSet);
        }

        return levelAndIds;
    }

    public Map<CourseLevel, Set<Integer>> getLevelAndIdMappingFor(Set<Integer> idSet) {
        Map<CourseLevel, Set<Integer>> levelAndIds = new HashMap<>();
        
        idSet.forEach(id->{
            Course course = this.courseRepository.findByAutoGenId(id);
            
            CourseLevel level = CourseLevel.ofDBString(course.getLevel().getLevel());
            
            if(levelAndIds.containsKey(level)){
                levelAndIds.get(level).add(course.getAutoGenId());
            }else{
                Set<Integer> sameLevelIdSet = new HashSet<>();
                sameLevelIdSet.add(course.getAutoGenId());
                
                levelAndIds.put(level, sameLevelIdSet);
            }
        });

        return levelAndIds;
    }

}
