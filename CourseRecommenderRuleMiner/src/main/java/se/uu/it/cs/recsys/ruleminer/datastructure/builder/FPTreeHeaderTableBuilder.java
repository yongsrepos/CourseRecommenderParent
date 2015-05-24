/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.uu.it.cs.recsys.ruleminer.datastructure.builder;

/*
 * #%L
 * CourseRecommenderRuleMiner
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.uu.it.cs.recsys.persistence.repository.CourseSelectionNormalizedRepository;
import se.uu.it.cs.recsys.ruleminer.datastructure.HeaderTableItem;
import se.uu.it.cs.recsys.ruleminer.datastructure.Item;
import se.uu.it.cs.recsys.ruleminer.util.Util;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class FPTreeHeaderTableBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPTreeHeaderTableBuilder.class);

    @Autowired
    private CourseSelectionNormalizedRepository courseSelectionNormalizedRepository;

    /**
     *
     * @param minSupport
     * @return non-null list; the list is empty if the min support requirement
     * is not met
     */
    public List<HeaderTableItem> buildHeaderTableFromDB(int minSupport) {
        Map<Integer, Integer> courseIdAndCount = new HashMap<>();

        int studentId = 1;
        final int maxStudentID = this.courseSelectionNormalizedRepository.findMaxCourseSelectionNormalizedPKStudentId();
        
        LOGGER.debug("Max studentId: {}", maxStudentID);

        while (studentId <= maxStudentID) {

            Set<Integer> allAttendedCourseIDs = this.courseSelectionNormalizedRepository
                    .findCourseSelectionNormalizedPKNormalizedCourseIdByCourseSelectionNormalizedPKStudentId(studentId);

            LOGGER.debug("Normalized attended coursed ids: {}, studentId: {}", allAttendedCourseIDs, studentId);

            allAttendedCourseIDs.forEach((courseId) -> {
                if (courseIdAndCount.containsKey(courseId)) {
                    Integer existingAmount = courseIdAndCount.get(courseId);
                    courseIdAndCount.put(courseId, existingAmount + 1);
                } else {
                    courseIdAndCount.put(courseId, 1);
                }
            });

            studentId++;
        }

        return build(courseIdAndCount, minSupport);
    }

    /**
     *
     * @param patternBase
     * @param minSupport
     * @return non-null list; the list is empty if the min support requirement
     * is not met
     */
    public static List<HeaderTableItem> buildFromPatternBase(Map<List<Integer>, Integer> patternBase,
            int minSupport) {
        Map<Integer, Integer> idAndCount = new HashMap<>();

        patternBase.entrySet().forEach(entry -> {

            List<Integer> allIds = entry.getKey();
            Integer count = entry.getValue();

            for (Integer id : allIds) {
                if (idAndCount.get(id) == null) {
                    idAndCount.put(id, count);
                    continue;
                }

                Integer existingCount = idAndCount.get(id);
                idAndCount.put(id, existingCount + count);
            }
        }
        );

        return build(idAndCount, minSupport);
    }

    /**
     *
     * @param idAndCount item id and count
     * @param threshold, min support
     * @return non-null HeaderTableItem if the input contains id meets threshold
     * requirement; otherwise null.
     */
    public static List<HeaderTableItem> build(Map<Integer, Integer> idAndCount, Integer threshold) {

        List<HeaderTableItem> instance = new ArrayList<>();

        Map<Integer, Integer> filteredIdAndCount = Util.filter(idAndCount, threshold);

        if (filteredIdAndCount.isEmpty()) {
            LOGGER.debug("Empty map after filtering. Empty list will be returned. Source: {}",
                    idAndCount);
            return instance;
        }

        List<Integer> countList = new ArrayList<>(filteredIdAndCount.values());
        Collections.sort(countList);
        Collections.reverse(countList);// now the count is in DESC order

        for (int i = 1; i <= filteredIdAndCount.size(); i++) {
            instance.add(new HeaderTableItem()); // in order to call list.set(idx,elem)
        }

        Map<Integer, Set<Integer>> forIdsHavingSameCount = new HashMap<>();//different ids may have same count

        filteredIdAndCount.entrySet().forEach((entry) -> {
            Integer courseId = entry.getKey();
            Integer count = entry.getValue();

            Integer countFrequence = Collections.frequency(countList, count);

            if (countFrequence == 1) {
                Integer countIdx = countList.indexOf(count);
                instance.set(countIdx, new HeaderTableItem(new Item(courseId, count)));
            } else {
                // different ids have same count
                
                if (!forIdsHavingSameCount.containsKey(count)) {
                    forIdsHavingSameCount.put(count, Util.findDuplicatesIndexes(countList, count));
                }

                Iterator<Integer> itr = forIdsHavingSameCount.get(count).iterator();
                Integer idx = itr.next();
                itr.remove();

                instance.set(idx, new HeaderTableItem(new Item(courseId, count)));
            }

        });

        LOGGER.debug("Final built header table: {}",
                instance.stream()
                .map(headerItem -> headerItem.getItem()).collect(Collectors.toList()));

        return instance;
    }

}
