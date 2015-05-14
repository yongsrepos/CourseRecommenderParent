
package se.uu.it.cs.recsys.ruleminer.impl;

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

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTree;
import se.uu.it.cs.recsys.ruleminer.datastructure.HeaderTableItem;
import se.uu.it.cs.recsys.ruleminer.datastructure.Item;
import se.uu.it.cs.recsys.ruleminer.datastructure.builder.FPTreeBuilder;
import se.uu.it.cs.recsys.ruleminer.util.FPTreeUtil;
import se.uu.it.cs.recsys.ruleminer.util.Util;

/**
 * Implement the FP-Growth Algorithm
 */
@Service
public class FPGrowthImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPGrowthImpl.class);

    public static final Integer DEFAULT_MIN_SUPPORT = 10;

    private Integer minSupport = DEFAULT_MIN_SUPPORT;

    /**
     *
     *
     * @param fpTree non-null FP Tree
     * @return all the frequent patterns that meets the requirement of the min
     * support
     * @throws IllegalArgumentException, if input is null
     */
    public Map<Set<Integer>, Integer> getAllFrequentPattern(FPTree fpTree) {

        if (fpTree == null) {
            throw new IllegalArgumentException("Input FP Tree is null!");
        }

        return miningWithFPGrowth(fpTree, Collections.<Integer>emptySet());
    }

    private Map<Set<Integer>, Integer> miningWithFPGrowth(FPTree fpTree, Set<Integer> suffixPattern) {

        Map<Set<Integer>, Integer> frequentPatternFromSinglePrefixPath = new HashMap<>();
        FPTree branchingTree = fpTree;

        if (fpTree.hasSinglePrefixPath()) {
            List<Item> singlePrefixPath = fpTree.getSinglePrefixPathInTopDownOrder();
            LOGGER.debug("Single prefix path: {}", singlePrefixPath);

            Map<Set<Integer>, Integer> frequentPatternWithinSinglePrefixPath
                    = getFrequentPatternFromSinglePrefixPath(singlePrefixPath);

            frequentPatternFromSinglePrefixPath = frequentPatternWithinSinglePrefixPath
                    .entrySet().stream()
                    .collect(
                            Collectors.toMap(entry -> {
                                Set<Integer> existingPattern = new HashSet<>(entry.getKey());
                                existingPattern.addAll(suffixPattern);
                                return existingPattern;
                            },
                            entry -> entry.getValue()));

            branchingTree = fpTree.getBranchingTree();

            if (branchingTree == null) {
                return frequentPatternFromSinglePrefixPath;
            }
        }

        Map<Set<Integer>, Integer> frequentPatternFromBranchingTree = new HashMap<>();

        List<HeaderTableItem> headerList = branchingTree.getHeaderTable();
        ListIterator<HeaderTableItem> itr = headerList.listIterator(headerList.size());

        while (itr.hasPrevious()) {
            HeaderTableItem visitingItem = itr.previous();

            Set<Integer> newPattern = new HashSet<>(suffixPattern);
            newPattern.add(visitingItem.getItem().getId());

            frequentPatternFromBranchingTree.put(newPattern, visitingItem.getItem().getCount());
            LOGGER.debug("Adding new pattern: {}, count: {}", newPattern, visitingItem.getItem().getCount());

            Map<List<Integer>, Integer> patternBase = FPTreeUtil.getPatternBase(visitingItem);
            LOGGER.debug("Pattern base for item {} is: {}", visitingItem.getItem(), patternBase);
            
            FPTree conditionalTree = FPTreeBuilder.buildConditionalFPTree(patternBase, this.minSupport);

            if (conditionalTree != null && !conditionalTree.getRoot().getChildren().isEmpty()) {
                frequentPatternFromBranchingTree.putAll(miningWithFPGrowth(conditionalTree, newPattern));
            }
        }

        return consolidatePatterns(frequentPatternFromSinglePrefixPath,
                frequentPatternFromBranchingTree);
    }

    private Map<Set<Integer>, Integer> consolidatePatterns(Map<Set<Integer>, Integer> patternFromSinglePrefixPath,
            Map<Set<Integer>, Integer> patternFromBranchingTree) {

        if (patternFromSinglePrefixPath.isEmpty()) {
            return patternFromBranchingTree;
        }

        Map<Set<Integer>, Integer> consolidated = new HashMap<>();
        consolidated.putAll(patternFromSinglePrefixPath);
        consolidated.putAll(patternFromBranchingTree);
        consolidated.putAll(getCrossProduct(patternFromSinglePrefixPath, patternFromBranchingTree));

        return consolidated;
    }

    private Map<Set<Integer>, Integer> getCrossProduct(Map<Set<Integer>, Integer> patternFromSinglePrefixPath,
            Map<Set<Integer>, Integer> patternFromBranchingTree) {

        Map<Set<Integer>, Integer> totalCrossProduct = new HashMap<>();

        patternFromSinglePrefixPath.entrySet()
                .stream().forEach(
                        entryFromPrefixPath -> {
                            patternFromBranchingTree.entrySet()
                            .stream().forEach(
                                    entryFromBranchingTree -> {

                                        Set<Integer> singleCrossProduct
                                        = new HashSet<>(entryFromBranchingTree.getKey());
                                        singleCrossProduct.addAll(entryFromPrefixPath.getKey());

                                        totalCrossProduct
                                        .put(singleCrossProduct, entryFromBranchingTree.getValue());
                                    });

                        });

        return totalCrossProduct;
    }

    /**
     * Call this method to give a customized min support. If this method is not
     * called, then the instance will use
     * {@link FPGrowthImpl#DEFAULT_MIN_SUPPORT} by default.
     *
     * @param minSupport, has to be a positive value.
     * @throws IllegalArgumentException if null or non-positive
     */
    public void setMinSupport(Integer minSupport) {

        if (minSupport == null || minSupport <= 0) {
            throw new IllegalArgumentException("Min support needs to be a positive value!");
        }

        this.minSupport = minSupport;
    }

    /**
     *
     * @param singlePrefixPath, ordered single prefix path from a FP Tree
     * @return pairs of (frequent pattern, its support); returns empty map if
     * input is null or empty
     */
    public static Map<Set<Integer>, Integer> getFrequentPatternFromSinglePrefixPath(List<Item> singlePrefixPath) {
        if (singlePrefixPath == null || singlePrefixPath.isEmpty()) {
            LOGGER.warn("Nonsence to give null or empty input. Do you agree?");
            return Collections.EMPTY_MAP;
        }

        Set<Item> itemSetFromPath = new HashSet<>(singlePrefixPath);

        Set<Set<Item>> powerSet = Sets.powerSet(itemSetFromPath);

        Map<Set<Integer>, Integer> r = new HashMap<>();

        Util.removeEmptySet(powerSet).forEach(itemSet -> {
            int localMinSupport = FPTreeUtil.getMinSupport(itemSet);

            r.put(itemSet.stream()
                    .map(item -> item.getId())
                    .collect(Collectors.toSet()),
                    localMinSupport);
        });

        return r;
    }
}
