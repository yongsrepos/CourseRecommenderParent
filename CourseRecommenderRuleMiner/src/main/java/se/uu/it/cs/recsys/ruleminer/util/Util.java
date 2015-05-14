
package se.uu.it.cs.recsys.ruleminer.util;

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


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.ruleminer.datastructure.Item;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static <T> Set<Integer> findDuplicatesIndexes(List<T> list, T elem) {
        Set<Integer> idxSet = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(elem)) {
                idxSet.add(i);
            }
        }
        return idxSet;
    }

    /**
     *
     * @param <T> the type of the element in the list
     * @param source source list
     * @param target the target to look for
     * @return 0 based index of the position where the target occurs in the list
     * for the first time; or -1 if no such position is found
     */
    public static <T> int findIndex(List<T> source, T target) {
        int matchingIdx = -1;
        int i = 0;
        for (T elem : source) {
            if (elem.equals(target)) {
                matchingIdx = i;
                break;
            }
            i++;
        }
        return matchingIdx;
    }

    /**
     * Generate a new list containing the items in both source list and
     * reference list; but sorted according to the order in reference list.
     *
     * @param source the source list
     * @param referenceOrder the reference list which has ordered items
     * @return a new list containing ordered items
     */
    public static List<Integer> reorderListAccordingToRef(List<Integer> source, List<Integer> referenceOrder) {
        Map<Integer, Integer> orderedMap = new TreeMap<>();
        source.forEach((Integer target) -> {
            int idx = findIndex(source, target);
            if (idx == -1) {
                LOGGER.error("target : {} is not in the ref list: {}", target, source);
            } else {
                orderedMap.put(idx, target);
            }
        });
        return orderedMap.entrySet().stream().map((Map.Entry<Integer, Integer> entry) -> entry.getValue()).collect(Collectors.toList());
    }

    public static Map<Integer, Integer> filter(Map<Integer, Integer> idAndCount, Integer threshold) {
        return idAndCount
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() >= threshold)
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    public static Set<Set<Item>> removeEmptySet(Set<Set<Item>> source) {
        return source.stream().filter((Set<Item> set) -> (!set.isEmpty())).collect(Collectors.toSet());
    }
}
