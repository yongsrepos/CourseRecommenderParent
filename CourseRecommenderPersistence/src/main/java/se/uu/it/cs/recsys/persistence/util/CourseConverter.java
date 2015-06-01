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
import se.uu.it.cs.recsys.api.type.ComputingDomain;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.persistence.entity.Course;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseConverter {

    public static se.uu.it.cs.recsys.api.type.Course convert(Course course) {
        se.uu.it.cs.recsys.api.type.Course resultCourse = new se.uu.it.cs.recsys.api.type.Course.Builder()
                .setCode(course.getCode())
                .setName(course.getName())
                .setCredit(course.getCredit().getCredit())
                .setLevel(CourseLevel.ofDBString(course.getLevel().getLevel()))
                .setTaughtYear((int) course.getTaughtYear())
                .setStartPeriod((int) course.getStartPeriod())
                .setEndPeriod((int) course.getEndPeriod())
                .build();

        course.getCourseDomainRelevanceCollection().forEach(rel -> {
            String domainId = rel.getComputingDomain().getId();
            String domainName = rel.getComputingDomain().getName();
            ComputingDomain domain = new ComputingDomain(domainId, domainName);

            resultCourse.addRelatedDomain(domain);
        });

        return resultCourse;
    }

}
