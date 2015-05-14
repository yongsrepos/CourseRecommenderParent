
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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public interface CourseRepository extends JpaRepository<Course, Integer> {

    public Set<Course> findByTaughtYearAndStartPeriod(@Param("taughtYear") Short taughtYear, @Param("startPeriod") Short startPeriod);
    
    public Course findByCodeAndTaughtYearAndStartPeriod(@Param("code") String code, @Param("taughtYear") Short taughtYear, @Param("startPeriod") Short startPeriod);

    public Course findByAutoGenId(@Param("autoGenId") Integer autoGenId);

    public Set<Course> findByCode(@Param("code") String code);

    public Set<Course> findByLevel(@Param("level") SupportedCourseLevel level);

    public Set<Course> findByCredit(@Param("credit") SupportedCourseCredit credit);
    
    

}
