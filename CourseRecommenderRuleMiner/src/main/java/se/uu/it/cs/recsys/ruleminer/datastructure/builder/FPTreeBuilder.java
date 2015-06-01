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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.persistence.entity.CourseSelectionNormalized;
import se.uu.it.cs.recsys.persistence.repository.CourseSelectionNormalizedRepository;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTree;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTreeNode;
import se.uu.it.cs.recsys.ruleminer.datastructure.HeaderTableItem;
import se.uu.it.cs.recsys.ruleminer.datastructure.Item;
import se.uu.it.cs.recsys.ruleminer.util.HeaderTableUtil;
import se.uu.it.cs.recsys.ruleminer.util.Util;

/**
 * The builder to build FP Tree from the transaction database.
 */
@Component
public class FPTreeBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPTreeBuilder.class);

    @Autowired
    private CourseSelectionNormalizedRepository courseSelectionNormalizedRepository;

    @Autowired
    private FPTreeHeaderTableBuilder fPTreeHeaderTableBuilder;

    public FPTreeBuilder() {
        // no-arg constructor
    }

    /**
     *
     * @param frequentBase
     * @param minSupport
     * @return instance or null if the min support requirement is not met
     */
    public static FPTree buildConditionalFPTree(Map<List<Integer>, Integer> frequentBase, int minSupport) {
        FPTree theTree = new FPTree();

        List<HeaderTableItem> headerTable = FPTreeHeaderTableBuilder.buildFromPatternBase(frequentBase, minSupport);

        if (headerTable.isEmpty()) {
            LOGGER.debug("The frequent base does not meet min support to build a conditional tree");
            return null;
        }
        theTree.setHeaderTable(headerTable);

        buildConditionalTreeBody(theTree, frequentBase);

        return theTree;
    }

    private static void buildConditionalTreeBody(FPTree theTree, Map<List<Integer>, Integer> frequentBase) {

        List<Integer> orderedFrequentIds = HeaderTableUtil.getOrderedItemId(theTree.getHeaderTable());

        frequentBase.entrySet().forEach(entry -> {
            List<Integer> ids = entry.getKey().stream().collect(Collectors.toList());

            List<Integer> orderedIds = Util.reorderListAccordingToRef(ids, orderedFrequentIds);

            insertTransData(theTree.getHeaderTable(), theTree.getRoot(), orderedIds);
        });
    }

    /**
     * Builds FP tree with transaction data from DB
     *
     * @param minSupport the minimum threshold support
     * @return instance of a FP tree or null if the threshold requirement does
     * not meet.
     */
    
    @Cacheable("FPTrees")
    public FPTree buildTreeFromDB(int minSupport) {
        FPTree theTree = new FPTree();

        List<HeaderTableItem> headerTable = this.fPTreeHeaderTableBuilder
                .buildHeaderTableFromDB(minSupport);

        if (headerTable.isEmpty()) {
            LOGGER.warn("The transaction data does not meet min support to build a FP tree!");
            return null;
        }
        theTree.setHeaderTable(headerTable);
        buildTreeBodyFromDB(theTree);

        return theTree;
    }

    public void buildTreeBodyFromDB(FPTree theTree) {
        List<Integer> orderedFrequentItemIds = HeaderTableUtil.getOrderedItemId(theTree.getHeaderTable());

        int i = 1;
        final int maxStudentID = this.courseSelectionNormalizedRepository
                .findMaxCourseSelectionNormalizedPKStudentId();

        while (i <= maxStudentID) {
            Set<CourseSelectionNormalized> courseSet = this.courseSelectionNormalizedRepository
                    .findByCourseSelectionNormalizedPKStudentId(i);

            List<Integer> attendedCourseIds = courseSet.stream()
                    .map(course -> course.getCourseSelectionNormalizedPK().getNormalizedCourseId())
                    .collect(Collectors.toList());

//            LOGGER.debug("Normalized attened courses: {}", attendedCourseIds);

            List<Integer> orderedCourseIds = Util.reorderListAccordingToRef(attendedCourseIds, orderedFrequentItemIds);

//            LOGGER.debug("Recordered normalized attened courses: {}", attendedCourseIds);

            insertTransData(theTree.getHeaderTable(),
                    theTree.getRoot(),
                    orderedCourseIds);

            i++;
        }
    }

    private static void insertTransData(List<HeaderTableItem> theHeaderTable,
            FPTreeNode rootNode, List<Integer> orderedTransItemIds) {

        if (orderedTransItemIds.isEmpty()) {
            LOGGER.debug("Empty transaction items set!");
            return;
        }

        Integer transHeadItemId = orderedTransItemIds.get(0);

        for (FPTreeNode node : rootNode.getChildren()) {
            if (node.getItem().getId().equals(transHeadItemId)) {
                // same item node exist as root node's child
                node.getItem().increaseCountBy(1);

                if (orderedTransItemIds.size() > 1) {
                    insertTransData(theHeaderTable, node,
                            orderedTransItemIds.subList(1, orderedTransItemIds.size()));
                }

                return;
            }
        }

        // node with same id may exist, but it's not root's child
        FPTreeNode newNode = new FPTreeNode(new Item(transHeadItemId, 1));
        newNode.setForwardSameItemNode(null);
        newNode.setParent(rootNode);
        rootNode.getChildren().add(newNode);

        HeaderTableItem headerItem = HeaderTableUtil.getTableItemByItemId(theHeaderTable, transHeadItemId);

        if (headerItem == null) {
            LOGGER.debug("item: {} does not meet minSupport requirement, trans {} is discarded.",
                    transHeadItemId, orderedTransItemIds);
            return;
        }

        if (headerItem.getNodeLinkHead() == null) {
            headerItem.setNodeLinkHead(newNode);
        } else {
            FPTreeNode sameItemNode = headerItem.getNodeLinkHead();

            while (sameItemNode.getForwardSameItemNode() != null) {
                sameItemNode = sameItemNode.getForwardSameItemNode();
            }

            sameItemNode.setForwardSameItemNode(newNode);
            newNode.setBackwardSameItemNode(sameItemNode);
        }

        if (orderedTransItemIds.size() > 1) {
            insertTransData(theHeaderTable, newNode,
                    orderedTransItemIds.subList(1, orderedTransItemIds.size()));
        }
    }
}
