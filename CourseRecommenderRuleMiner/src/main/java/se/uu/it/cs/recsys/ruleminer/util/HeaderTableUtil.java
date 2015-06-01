
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


import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.ruleminer.datastructure.HeaderTableItem;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class HeaderTableUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderTableUtil.class);

    public static List<Integer> getOrderedItemId(List<HeaderTableItem> headerTable) {
        return headerTable.stream()
                .map(tableItem -> tableItem.getItem().getId())
                .collect(Collectors.toList());
    }

    /**
     *
     * @param headerTable non-null header table
     * @param id, non-null id of an item.
     * @return the found table item or null if not found
     * @throws IllegalArgumentException, if input is null;
     */
    public static HeaderTableItem getTableItemByItemId(List<HeaderTableItem> headerTable, Integer id) {
        if (headerTable == null || id == null) {
            throw new IllegalArgumentException("item id can not be null!");
        }

        HeaderTableItem result = null;
        for (HeaderTableItem item : headerTable) {
            if (item.getItem().getId().equals(id)) {
                result = item;
            }
        }

//        if (result == null) {
//            LOGGER.debug("No match item found for id: {} in table: {}", id,
//                    headerTable.stream().map(headItem -> headItem.getItem()).collect(Collectors.toList()));
//        }

        return result;
    }

}
