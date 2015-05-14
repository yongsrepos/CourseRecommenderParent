
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

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class Item {

    private Integer id;
    private int count;

    private Item() {
        // no-arg
    }

    /**
     * @param id non-null input
     * @throws IllegalArgumentException if input is null
     */
    public Item(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("input can not be null");
        }
        this.id = id;
    }

    /**
     * @param id non-null input
     * @param count the count of the item
     * @throws IllegalArgumentException if input id is null
     */
    public Item(Integer id, int count) {
        if (id == null) {
            throw new IllegalArgumentException("input can not be null");
        }

        this.id = id;
        this.count = count;
    }

    public Integer getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public void increaseCountBy(int increament){
        this.count = this.count + increament;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + this.count;
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
        final Item other = (Item) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.count != other.count) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FPTreeItem{" + "id=" + id + ", count=" + count + '}';
    }
    
}
