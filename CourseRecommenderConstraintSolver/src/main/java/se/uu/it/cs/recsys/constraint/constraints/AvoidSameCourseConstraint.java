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


import java.util.Set;
import org.jacop.constraints.XlteqC;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.set.constraints.CardA;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.constraint.util.Util;

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
    public static void impose(Store store, SetVar unionVar, Set<Set<Integer>> idSetForSameCourse) {
        LOGGER.info("Posting constraints to avoid seleting same course!");
        
        final int minUnionVarCard = 1;
        store.impose(new CardA(unionVar, minUnionVarCard, unionVar.dom().lub().getSize()));
        
        idSetForSameCourse
                .forEach((Set<Integer> idSet) -> {
                    
                    IntVar intersectionCardVar = Util.getIntersectionCardVar(store, unionVar, idSet);
                    
                    final int maxIntersectionCard = 1;
                    store.impose(new XlteqC(intersectionCardVar, maxIntersectionCard));
                });
    }

}
