
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTree;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTreeNode;
import se.uu.it.cs.recsys.ruleminer.datastructure.HeaderTableItem;
import se.uu.it.cs.recsys.ruleminer.datastructure.Item;

/**
 *
 */
public class FPTreeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPTreeUtil.class);

    /**
     * A pattern base is a prefix in a prefix tree. For the given node, this
     * method returns all the paths, prefixing the nodes sharing same item of
     * the input node.
     *
     * @param nodeInHeaderTable the head node of a given item.
     * @return complete ordered prefix path set, with its corresponding visiting
     * count.
     */
    public static Map<List<Integer>, Integer> getPatternBase(HeaderTableItem nodeInHeaderTable) {
        Map<List<Integer>, Integer> result = new HashMap<>();

        FPTreeNode currentNodeFromTreeBody = nodeInHeaderTable.getNodeLinkHead();

        while (currentNodeFromTreeBody != null) {
            List<Integer> patternPath = getPatternPath(currentNodeFromTreeBody);

            if (!patternPath.isEmpty()) {
                result.put(patternPath, currentNodeFromTreeBody.getItem().getCount());
            }

            currentNodeFromTreeBody = currentNodeFromTreeBody.getForwardSameItemNode();
        }

        return result;
    }

    /**
     *
     * @param leafNode target node
     * @return the ordered path from root (excluded) to the direct parent node
     * of the input node; if the input node's parent is root, then an empty list
     * is returned.
     * @throws IllegalArgumentException if input is null
     */
    public static List<Integer> getPatternPath(FPTreeNode leafNode) {
        if (leafNode == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }

        List<Integer> thePath = new ArrayList<>();

        FPTreeNode parentNode = leafNode.getParent();

        while (!parentNode.getItem().getId().equals(FPTree.ROOT_ITEM_ID)) {
            thePath.add(parentNode.getItem().getId());
            parentNode = parentNode.getParent();
        }

        if (!thePath.isEmpty()) {
            Collections.reverse(thePath);
        }

        return thePath;
    }

    /**
     *
     * @param itemList non-null, non-empty list
     * @return min support of the item from the input list
     * @throws IllegalArgumentException if input is null or empty
     */
    public static int getMinSupport(List<Item> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            throw new IllegalArgumentException("Nonsense to have null or emtpy as input!");
        }
        int support = itemList.get(0).getCount();

        for (Item item : itemList) {
            if (item.getCount() <= support) {
                support = item.getCount();
            }
        }

        return support;
    }

    /**
     *
     * @param itemSet non-null, non-empty set
     * @return min support of the item from the input set
     * @throws IllegalArgumentException if input is null or empty
     */
    public static int getMinSupport(Set<Item> itemSet) {
        return getMinSupport(new ArrayList<>(itemSet));

    }

    public static StringBuilder printTree(FPTreeNode root) {
        StringBuilder treeStructure = new StringBuilder();

        if (root.getParent() == null) {
            treeStructure.append("root").append(" children: ")
                    .append(root.getChildren().size())
                    .append("\n");
        }

        if (root.getChildren().isEmpty()) {

            return treeStructure;
        }

        int i = 1;
        for (FPTreeNode child : root.getChildren()) {
            treeStructure.append(child.getItem().toString())
                    .append("children: ")
                    .append(root.getChildren().size())
                    .append("pos:  ")
                    .append(i)
                    .append("  ");

            if (i == root.getChildren().size()) {
                treeStructure.append("\n");
            }

            i++;
        }

        root.getChildren()
                .forEach((child) -> {
                    treeStructure.append(printTree(child));
                });

        return treeStructure;
    }
}
