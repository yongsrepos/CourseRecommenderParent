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
import org.jacop.core.IntervalDomain;
import org.jacop.core.SmallDenseDomain;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;

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
    public static SetDomain createDomain(Set<Integer> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException("Requires non-empty input!");
        }

        List<Integer> elemts = new ArrayList<>(elements);
        Collections.sort(elemts);

        IntDomain lub = new IntervalDomain();

        for (int i : elements) {
            lub = lub.union(i);
        }

        return new BoundSetDomain(IntDomain.emptyIntDomain, lub);
    }

    public static IntDomain createIntDomain(Set<Integer> elements) {
        if (elements == null || elements.isEmpty()) {
            throw new IllegalArgumentException("Requires non-empty input!");
        }

        List<Integer> elemts = new ArrayList<>(elements);
        Collections.sort(elemts);

        IntDomain result = new IntervalDomain();

        for (Integer elem : elemts) {
            result = result.union(elem);
        }

        return result;
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

        IntDomain result = new IntervalDomain();

        for (int i = interval.min(); i <= interval.max();) {
            result = result.union(i);
            i = i + step;
        }

        if (((interval.max() - interval.min()) % step) != 0) {
            result = result.union(interval.max());
        }

        return result;
    }
}
