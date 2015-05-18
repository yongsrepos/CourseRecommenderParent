package se.uu.it.cs.recsys.constraint.constraints;

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
import java.util.List;
import java.util.Set;
import org.jacop.constraints.Sum;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.SmallDenseDomain;
import org.jacop.core.Store;
import org.jacop.set.constraints.AintersectBeqC;
import org.jacop.set.constraints.CardA;
import org.jacop.set.constraints.CardAeqX;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class AvoidSameCourseConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvoidSameCourseConstraint.class);

    /**
     * 
     * @param store
     * @param unionVar, requires Card(unionVar) >= 1 
     * @param idSetForSameCourse 
     */
    public static void impose(Store store, SetVar unionVar, List<Set<Integer>> idSetForSameCourse) {
        LOGGER.info("Posting constraints to avoid seleting same course!");
        
        store.impose(new CardA(unionVar, 1, unionVar.dom().lub().getSize()));
        
        idSetForSameCourse
                .forEach((Set<Integer> idSet) -> {
                    LOGGER.debug("Id set {}", idSet);

                    ArrayList<IntVar> cardVarList = new ArrayList<>();

                    idSet.forEach((Integer id) -> {

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
                                = new AintersectBeqC(unionVar, 
                                        singleIdVar, 
                                        intersectVar);
                        store.impose(intersectConst);

                        IntVar cardVar = new IntVar(store, 0, 1);
                        CardAeqX cardConst = new CardAeqX(intersectVar, cardVar);
                        store.impose(cardConst);

                        cardVarList.add(cardVar);
                    });

                    IntVar totalCardForSameCourse = new IntVar(store, 0, 1);

                    Sum cardSumConst = new Sum(cardVarList, totalCardForSameCourse);

                    store.impose(cardSumConst);
                });
    }

}
