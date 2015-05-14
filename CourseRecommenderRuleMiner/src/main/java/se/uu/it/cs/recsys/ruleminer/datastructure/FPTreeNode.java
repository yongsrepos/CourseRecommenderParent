
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
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class FPTreeNode {

    private Item item;
    private final List<FPTreeNode> children = new ArrayList<>();
    private FPTreeNode parent;
    private FPTreeNode forwardSameItemNode;
    private FPTreeNode backwardSameItemNode;

    /**
     * @param item the item in this node, non-null.
     *
     * @throws IllegalArgumentException if input is null;
     */
    public FPTreeNode(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }
        this.item = item;
        this.parent = null;
        this.forwardSameItemNode = null;
        this.backwardSameItemNode = null;
    }

    private FPTreeNode() {
        // private non-arg
    }

    public Item getItem() {
        return item;
    }

    public List<FPTreeNode> getChildren() {
        return children;
    }

    public void addChild(FPTreeNode child) {
        this.children.add(child);
    }

    public FPTreeNode getParent() {
        return parent;
    }

    public void setParent(FPTreeNode parent) {
        this.parent = parent;
    }

    public FPTreeNode getForwardSameItemNode() {
        return forwardSameItemNode;
    }

    public void setForwardSameItemNode(FPTreeNode forwardSameItemNode) {
        this.forwardSameItemNode = forwardSameItemNode;
    }

    public FPTreeNode getBackwardSameItemNode() {
        return backwardSameItemNode;
    }

    public void setBackwardSameItemNode(FPTreeNode backwardSameItemNode) {
        this.backwardSameItemNode = backwardSameItemNode;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.item);
        hash = 11 * hash + Objects.hashCode(this.children);
        hash = 11 * hash + Objects.hashCode(this.parent);
        hash = 11 * hash + Objects.hashCode(this.forwardSameItemNode);
        hash = 11 * hash + Objects.hashCode(this.backwardSameItemNode);
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
        final FPTreeNode other = (FPTreeNode) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.forwardSameItemNode, other.forwardSameItemNode)) {
            return false;
        }
        if (!Objects.equals(this.backwardSameItemNode, other.backwardSameItemNode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FPTreeNode{" + "item=" + item ;
    }

}
