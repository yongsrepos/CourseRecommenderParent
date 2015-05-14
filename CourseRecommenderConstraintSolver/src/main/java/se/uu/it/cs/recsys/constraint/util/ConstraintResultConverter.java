
package se.uu.it.cs.recsys.constraint.util;

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
import org.jacop.set.core.SetDomainValueEnumeration;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 */
@Service
public class ConstraintResultConverter {
    
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConstraintResultConverter.class);
    
    @Autowired
    private CourseRepository courseRepository;
    
    public Map<Integer, Set<Course>> convert(SetVar[] in) {
        Map<Integer, Set<Course>> result = new HashMap<>();
        
        if (in == null || in.length == 0) {
            LOGGER.debug("Input result is empty!");
            return result;
        }
        
        int i = 1;
        for (SetVar var : in) {
            LOGGER.debug("SetVar to be converted: " + var.toString());
            
            if (var.dom() == null || var.dom().isEmpty()) {
                LOGGER.debug("The {}th var domain is empty.", i);
                i++;
                continue;
            }
            
            SetDomainValueEnumeration ve = (SetDomainValueEnumeration) (var
                    .dom().valueEnumeration());
            
            Set<Integer> courseIds = new HashSet<>();
            
            while (ve.hasMoreElements()) {
                int[] elemArray = ve.nextSetElement().toIntArray();
                                
                for(int elem:elemArray){
                    courseIds.add(elem);
                }      
            }
                        
            Set<Course> courseInfoSet = new HashSet<>();
            courseIds.forEach(id -> {
                Course course = this.courseRepository.findByAutoGenId(id);
                courseInfoSet.add(course);
                
            });
            
            result.put(i, courseInfoSet);
            
            i++;
        }
        
        return result;
    }
}
