
package se.uu.it.cs.recsys.ruleminer.datastructure;

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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.ruleminer.util.HeaderTableUtil;

/**
 *
 */
public class FPTree {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPTree.class);

    public static final Integer ROOT_ITEM_ID = -1;
    private FPTreeNode root;

    private List<HeaderTableItem> headerTable = new ArrayList<>();

    /**
     * The default non-arg constructor, with {@link #root} assigned instance
     * created by {@link FPTreeNode#FPTreeNode()}, name be {@link #ROOT_ITEM_ID}
     * and {@link #headerTable} assigned instance created by
     * {@link FPTreeHeaderTable#FPTreeHeaderTable()}
     */
    public FPTree() {
        this.root = new FPTreeNode(new Item(ROOT_ITEM_ID));
    }

    /**
     *
     * @return non-null root node
     */
    public FPTreeNode getRoot() {
        return root;
    }

    /**
     *
     * @return non-null node. If no single prefix path, then the root node will
     * be returned.
     */
    public FPTreeNode getBranchingNode() {
        FPTreeNode branchingNode = root;

        while (branchingNode.getChildren().size() == 1) {
            branchingNode = branchingNode.getChildren().get(0);
        }

        return branchingNode;
    }

    public boolean hasSinglePrefixPath() {
        return (!ROOT_ITEM_ID.equals(getBranchingNode().getItem().getId()));
    }

    public List<Item> getSinglePrefixPathInTopDownOrder() {
        FPTreeNode branchingNode = getBranchingNode();

        if (branchingNode.equals(this.root)) {
            return Collections.EMPTY_LIST;
        }

        List<Item> bottomUpPath = new ArrayList<>();
        bottomUpPath.add(branchingNode.getItem());

        FPTreeNode leadingNode = branchingNode.getParent();

        while (!leadingNode.equals(root)) {
            bottomUpPath.add(leadingNode.getItem());
            leadingNode = leadingNode.getParent();
        }

        Collections.reverse(bottomUpPath);

        return bottomUpPath;//already reversed; now it's top down.
    }

    /**
     *
     * @return the branching tree if has any or null if no such branching tree ;
     * @throws IllegalStateException if there is branching node, but its id is
     * not found in header table
     */
    public FPTree getBranchingTree() {
        if (!hasSinglePrefixPath()) {
            return this;
        }

        FPTreeNode branchingNode = getBranchingNode();

        if (branchingNode.getChildren().isEmpty()) {
            return null;
        }

        FPTree branchingTree = new FPTree();

        FPTreeNode branchingTreeRootNode = new FPTreeNode(new Item(FPTree.ROOT_ITEM_ID));
        branchingTreeRootNode.getChildren().addAll(branchingNode.getChildren());
        branchingTree.setRoot(branchingTreeRootNode);

        int branchingNodeItemId = branchingNode.getItem().getId();
        HeaderTableItem headerItem = HeaderTableUtil.getTableItemByItemId(this.headerTable, branchingNodeItemId);

        if (headerItem == null) {
            IllegalStateException ex = new IllegalStateException("Branching node item id is not in header table");
            LOGGER.error("Branching node item id: {} is not in header table: {}", branchingNodeItemId,
                    this.headerTable.stream().map(h -> h.getItem()).collect(Collectors.toList()), ex);
            throw ex;
        }

        int branchingNodeItemIdIdxInOrigHeaderTable = this.headerTable.indexOf(headerItem);

        branchingTree.setHeaderTable(this.headerTable
                .subList(branchingNodeItemIdIdxInOrigHeaderTable + 1,
                        this.headerTable.size()));

        branchingTree.setHeaderTable(headerTable);

        return branchingTree;
    }

    public void setRoot(FPTreeNode root) {
        this.root = root;
    }

    public List<HeaderTableItem> getHeaderTable() {
        return headerTable;
    }

    public void setHeaderTable(List<HeaderTableItem> headerTable) {
        this.headerTable = headerTable;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.root);
        hash = 97 * hash + Objects.hashCode(this.headerTable);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FPTree other = (FPTree) obj;
        if (!Objects.equals(this.root, other.root)) {
            return false;
        }
        if (!Objects.equals(this.headerTable, other.headerTable)) {
            return false;
        }
        return true;
    }
    
}
