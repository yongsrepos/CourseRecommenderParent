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
     * @param periodsInfo
     * @return SetDomain for the 6 study periods
     */
    public Map<Integer, SetDomain> createScheduleSetDomainsFor(Set<Integer> interestedCourseIdSet, Set<CourseSchedule> periodsInfo) {

        return getStartPeriodAndIdSetMapping(periodsInfo).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    Set<Integer> planedCourseIdSet = entry.getValue();

                                    planedCourseIdSet.retainAll(interestedCourseIdSet);//get intersection

                                    return planedCourseIdSet.isEmpty() ? SetDomain.emptyDomain
                                            : DomainBuilder.createDomain(planedCourseIdSet);
                                }));
    }

    /**
     * For period 1 to 6. Periods 5, 6 are considered to have same domain as 1,
     * 2 respectively.
     *
     * @return SetDomain for the 6 study periods
     */
    public Map<Integer, SetDomain> createScheduleSetDomains(Set<CourseSchedule> periodsInfo) {

        Map<Integer, SetDomain> periodIdAndScheduleSetDomain = new HashMap<>();

        Map<Integer, Set<Integer>> periodIdAndCourseIds = getStartPeriodAndIdSetMapping(periodsInfo);

        periodIdAndCourseIds.entrySet().stream().forEach(
                entry -> {
                    SetDomain domain = DomainBuilder.createDomain(entry.getValue());

                    periodIdAndScheduleSetDomain.put(entry.getKey(), domain);
                }
        );

        return periodIdAndScheduleSetDomain;
    }

    /**
     * For period 1 to 6. Periods 5, 6 are considered to have same domain as 1,
     * 2 respectively.
     *
     * @param periodsInfo
     * @return
     */
    public Map<Integer, Set<Integer>> getStartPeriodAndIdSetMapping(Set<CourseSchedule> periodsInfo) {


        return getTaughtYearAndStartPeriodToIdMapping(periodsInfo)
                .entrySet().stream()
                .collect(Collectors
                        .toMap(periodInfo -> {
                            return (int) periodInfo.getKey().getPeriodIdxAmongAllPlanPeriods();
                        },
                        Map.Entry::getValue));
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
