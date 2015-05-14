
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

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HeaderTableItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderTableItem.class);

    /**
     * @return non-null list of item ids, following top-down(by support) order.
     */
    private Item item;
    private FPTreeNode nodeLinkHead;

    /**
     *
     * @param item non-null input
     * @throws IllegalArgumentException if input is null
     */
    public HeaderTableItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }
        this.item = item;
    }

    public HeaderTableItem() {
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public FPTreeNode getNodeLinkHead() {
        return nodeLinkHead;
    }

    public void setNodeLinkHead(FPTreeNode nodeLinkHead) {
        this.nodeLinkHead = nodeLinkHead;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.item);
        hash = 89 * hash + Objects.hashCode(this.nodeLinkHead);
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
        final HeaderTableItem other = (HeaderTableItem) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        if (!Objects.equals(this.nodeLinkHead, other.nodeLinkHead)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HeaderTableItem{" + "item=" + item + '}';
    }
    
}
