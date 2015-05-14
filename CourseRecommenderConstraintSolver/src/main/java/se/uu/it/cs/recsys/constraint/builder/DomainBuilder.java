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
package se.uu.it.cs.recsys.constraint.builder;

/*
 * #%L
 * CourseRecommenderConstraintSolver
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
import java.util.Set;
import org.jacop.core.BoundDomain;
import org.jacop.core.IntDomain;
import org.jacop.core.Interval;
import org.jacop.core.SmallDenseDomain;
import org.jacop.set.core.BoundSetDomain;

/**
 *
 */
public class DomainBuilder {

    public static final int SMALL_DENSE_DOMAIN_ELEMENT_AMOUNT_LIMIT = 64;

    /**
     *
     * @param elements non-empty input of integer set
     * @return a domain with elements.
     * @throws IllegalArgumentException if input is null or empty
     */
    public static BoundSetDomain createDomain(Set<Integer> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException("Requires non-empty input!");
        }

        List<Integer> elemts = new ArrayList<>(elements);
        Collections.sort(elemts);

        BoundSetDomain domain = new BoundSetDomain();

        elemts.forEach((i) -> {
            domain.addDom(createSingleElementDomain(i));
        });

        return domain;
    }

    /**
     *
     * @param interval
     * @param step
     * @return instance of {@link SmallDenseDomain} if the element count from
     * the input is less or equal to
     * {@link #SMALL_DENSE_DOMAIN_ELEMENT_AMOUNT_LIMIT}; otherwise, a
     * {@link BoundDomain}
     */
    public static IntDomain createIntDomain(Interval interval, int step) {
        int elemCount = countElement(interval, step);

        IntDomain result;

        if (elemCount <= SMALL_DENSE_DOMAIN_ELEMENT_AMOUNT_LIMIT) {
            result = new SmallDenseDomain();
        } else {
            result = new BoundDomain();
        }

        for (int i = interval.min(); i <= interval.max();) {
            result.unionAdapt(i);
            i = i + step;
        }
        
        if(((interval.max() - interval.min())%step)!=0){
            result.unionAdapt(interval.max());
        }

        return result;
    }

    /**
     *
     * @param interval, the [min, max] interval
     * @param step the step
     * @return elements by walking through the element with step, bounderies
     * inclusive
     */
    public static int countElement(Interval interval, int step) {
        int quotient = (interval.max() - interval.min()) / step;
        int remainder = (interval.max() - interval.min()) % step;
        if (remainder == 0) {
            return quotient + 1;
        } else {
            return quotient + 2;
        }
    }

    /**
     *
     * @param i the single element
     * @return instance of {@link SmallDenseDomain} containing only one element
     */
    public static SmallDenseDomain createSingleElementDomain(int i) {
        SmallDenseDomain intDomain = new SmallDenseDomain();
        intDomain.unionAdapt(i);
        return intDomain;
    }
}
