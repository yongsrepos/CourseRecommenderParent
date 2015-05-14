
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


import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.persistence.config.PersistenceSpringConfig;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceSpringConfig.class)
public class CourseRepositoryIT {

    private final static Logger LOGGER = LoggerFactory.getLogger(CourseRepository.class);

    @Autowired
    private CourseRepository courseRepository;

    public CourseRepositoryIT() {
    }

    @Test
    public void testFindByCodeAndTaughtYearAndStartPeriod() {
    }

    @Test
    public void testFindByCode() {
    }

    @Test
    public void testFindByLevel() {
        Set<Course> advanced = this.courseRepository.findByLevel(new SupportedCourseLevel(CourseLevel.ADVANCED.name()));

        Assert.assertTrue(!advanced.isEmpty());
    }

    @Test
    public void testFindByCredit() {
        Set<Course> fiveCredits = this.courseRepository.findByCredit(new SupportedCourseCredit((double) CourseCredit.FIVE.getCredit()));

        Assert.assertTrue(!fiveCredits.isEmpty());
    }

    @Test
    public void testFindByTaughtYearAndStartPeriod() {
    }

    @Test
    public void testFindByAutoGenId() {
    }

    @Test
    public void testFindByAutoGenIds() {
        Set<Integer> ids = Stream.of(1,2,3,4,5).collect(Collectors.toSet());
        
        Assert.assertTrue(!this.courseRepository.findByAutoGenIds(ids).isEmpty());
    }
}
