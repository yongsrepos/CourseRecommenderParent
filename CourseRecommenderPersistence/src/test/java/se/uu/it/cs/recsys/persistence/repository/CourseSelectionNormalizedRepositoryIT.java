
package se.uu.it.cs.recsys.persistence.repository;

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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.uu.it.cs.recsys.persistence.config.PersistenceSpringConfig;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceSpringConfig.class)
public class CourseSelectionNormalizedRepositoryIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseSelectionNormalizedRepositoryIT.class);
    
    @Autowired
    CourseSelectionNormalizedRepository courseSelectionNormalizedRepository;
       
    
    public CourseSelectionNormalizedRepositoryIT() {
    }

    @Test
    public void testFindMaxStudentId() {
        LOGGER.info("**** max student id: {}", this.courseSelectionNormalizedRepository.findMaxCourseSelectionNormalizedPKStudentId());
    }


    @Test
    public void testFindByStudentId() {
         LOGGER.info("**** course selection set: {}", this.courseSelectionNormalizedRepository.findByCourseSelectionNormalizedPKStudentId(96));
    }
    
    @Test
    public void testFindCourseIdByStudentId() {
         LOGGER.info("**** course ID set: {}", this.courseSelectionNormalizedRepository.findCourseSelectionNormalizedPKNormalizedCourseIdByCourseSelectionNormalizedPKStudentId(96));
    }

//    public class CourseSelectionNormalizedRepositoryImpl implements CourseSelectionNormalizedRepository {
//
//        public Integer findMaxStudentId() {
//            return null;
//        }
//
//        public Set<CourseSelectionNormalized> findByStudentId(Integer studentId) {
//            return null;
//        }
//
//        public Set<Integer> findCourseIdByStudentId(Integer studentId) {
//            return null;
//        }
//    }
//    
}
