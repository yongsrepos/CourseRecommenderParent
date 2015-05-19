package se.uu.it.cs.recsys.constraint.util;

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
import java.util.Set;
import org.jacop.constraints.Sum;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Interval;
import org.jacop.core.SmallDenseDomain;
import org.jacop.core.Store;
import org.jacop.set.constraints.AintersectBeqC;
import org.jacop.set.constraints.CardA;
import org.jacop.set.constraints.CardAeqX;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class Util {

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
     * Posts constraints to ensure the input source Var has at least one element
     * and the result IntVar is the count of the elements in the intersection.
     *
     * @param store
     * @param sourceVar, source SetVar with at least one element
     * @param target
     * @return the count of the intersection between the sourceVar and target,
     * with a dom [0, target.size()]
     * @throws IllegalArgumentException if input target is null or empty
     */
    public static IntVar getIntersectionCardVar(Store store, SetVar sourceVar, Set<Integer> target) {

        if (target == null || target.isEmpty()) {
            throw new IllegalArgumentException("Does not make sense to have a null or empty input!");
        }

        store.impose(new CardA(sourceVar, 1, sourceVar.dom().lub().getSize()));

        ArrayList<IntVar> cardVarList = new ArrayList<>();

        target.forEach((Integer id) -> {

            IntDomain intDomain = new SmallDenseDomain();

            IntDomain glb = intDomain.union(id);
            IntDomain lub = intDomain.union(id);

            BoundSetDomain setDomain = new BoundSetDomain(glb, lub);

            SetVar singleIdVar = new SetVar(store, setDomain);

            SetDomain intersectionDom
                    = new BoundSetDomain(IntDomain.emptyIntDomain,
                            singleIdVar.dom().lub());

            SetVar intersectVar = new SetVar(store, intersectionDom);

            AintersectBeqC intersectConst
                    = new AintersectBeqC(sourceVar,
                            singleIdVar,
                            intersectVar);
            store.impose(intersectConst);

            IntVar cardVar = new IntVar(store, 0, 1);
            CardAeqX cardConst = new CardAeqX(intersectVar, cardVar);
            store.impose(cardConst);

            cardVarList.add(cardVar);
        });

        IntVar totalCardVar = new IntVar(store, 0, target.size());

        Sum cardSumConst = new Sum(cardVarList, totalCardVar);

        store.impose(cardSumConst);

        return totalCardVar;
    }

}
