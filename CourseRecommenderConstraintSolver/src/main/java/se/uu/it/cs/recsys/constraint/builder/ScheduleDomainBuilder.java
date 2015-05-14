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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jacop.set.core.SetDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseSchedule;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;

/**
 *
 */
@Component
public class ScheduleDomainBuilder {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * For period 1 to 6. Periods 5, 6 are considered to have same domain as 1,
     * 2 respectively.
     *
     * @param interestedCourseIdSet
     * @return SetDomain for the 6 study periods
     */
    public Map<Integer, SetDomain> createScheduleSetDomainsFor(Set<Integer> interestedCourseIdSet) {

        Map<Integer, SetDomain> periodIdAndScheduleSetDomain = new HashMap<>();

        Map<Integer, Set<Integer>> periodIdAndCourseIds = getStartPeriodAndIdSetMapping();

        periodIdAndCourseIds.entrySet().stream().forEach(entry -> {
            Set<Integer> idIntersection = entry.getValue();
            idIntersection.retainAll(interestedCourseIdSet);

            SetDomain domain = DomainBuilder.createSetDomain(idIntersection);

            periodIdAndScheduleSetDomain.put(entry.getKey(), domain);
        });

        return periodIdAndScheduleSetDomain;
    }

    /**
     * For period 1 to 6. Periods 5, 6 are considered to have same domain as 1,
     * 2 respectively.
     *
     * @return SetDomain for the 6 study periods
     */
    public Map<Integer, SetDomain> createScheduleSetDomains() {

        Map<Integer, SetDomain> periodIdAndScheduleSetDomain = new HashMap<>();

        Map<Integer, Set<Integer>> periodIdAndCourseIds = getStartPeriodAndIdSetMapping();

        periodIdAndCourseIds.entrySet().stream().forEach(
                entry -> {
                    SetDomain domain = DomainBuilder.createSetDomain(entry.getValue());

                    periodIdAndScheduleSetDomain.put(entry.getKey(), domain);
                }
        );

        return periodIdAndScheduleSetDomain;
    }

    /**
     * For period 1 to 6. Periods 5, 6 are considered to have same domain as 1,
     * 2 respectively.
     *
     * @return
     */
    public Map<Integer, Set<Integer>> getStartPeriodAndIdSetMapping() {

        Map<Integer, Set<Integer>> scheduledIntIds = new HashMap<>();

        CourseSchedule year2015p1 = new CourseSchedule((short) 2015, (short) 1);
        CourseSchedule year2015p2 = new CourseSchedule((short) 2015, (short) 2);
        CourseSchedule year2016p3 = new CourseSchedule((short) 2016, (short) 3);
        CourseSchedule year2016p4 = new CourseSchedule((short) 2016, (short) 4);

        Set<CourseSchedule> schedules = Stream.of(year2015p1, year2015p2, year2016p3, year2016p4)
                .collect(Collectors.toSet());

        Map<CourseSchedule, Set<Integer>> scheduleToCourseIdMapping
                = getTaughtYearAndStartPeriodToIdMapping(schedules);

        scheduledIntIds.put(1, scheduleToCourseIdMapping.get(year2015p1));
        scheduledIntIds.put(2, scheduleToCourseIdMapping.get(year2015p2));
        scheduledIntIds.put(3, scheduleToCourseIdMapping.get(year2016p3));
        scheduledIntIds.put(4, scheduleToCourseIdMapping.get(year2016p4));
        scheduledIntIds.put(5, scheduleToCourseIdMapping.get(year2015p1));
        scheduledIntIds.put(6, scheduleToCourseIdMapping.get(year2015p2));

        return scheduledIntIds;
    }

    private Map<CourseSchedule, Set<Integer>> getTaughtYearAndStartPeriodToIdMapping(Set<CourseSchedule> schedule) {

        Map<CourseSchedule, Set<Integer>> taughtYearAndStartPeriodToIdMapping = new HashMap<>();

        schedule.forEach(sch -> {
            Set<Course> courseSet = this.courseRepository
                    .findByTaughtYearAndStartPeriod(sch.getTaughtYear(), sch.getStartPeriod());

            Set<Integer> idSet = courseSet.stream()
                    .map(c -> c.getAutoGenId())
                    .collect(Collectors.toSet());

            taughtYearAndStartPeriodToIdMapping.put(sch, idSet);
        });

        return taughtYearAndStartPeriodToIdMapping;
    }
}
