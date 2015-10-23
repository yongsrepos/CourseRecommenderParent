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
import org.jacop.constraints.Sum;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.set.constraints.CardAeqX;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class TotalCourseCardinalityConstraint {


    private static final Logger LOGGER = LoggerFactory.getLogger(TotalCourseCardinalityConstraint.class);

    public static void impose(Store store, SetVar[] periodVars, int maxCourseAmount) {
        LOGGER.debug("Posting cardinality constraint for all study periods.");

        ArrayList<IntVar> cardList = new ArrayList<>();

        for (SetVar var : periodVars) {
            IntVar cardVar = new IntVar(store, 
                    ConstraintSolverPreference.MIN_COURSE_AMOUNT_EACH_PERIOD, 
                    ConstraintSolverPreference.MAX_COURSE_AMOUNT_EACH_PERIOD);

            CardAeqX cardConstraint = new CardAeqX(var, cardVar);
            store.impose(cardConstraint);

            cardList.add(cardVar);
        }

        IntVar totalCard = new IntVar(store, 
                ConstraintSolverPreference.MIN_TOTAL_COURSE_AMOUNT_DEFAULT, 
                maxCourseAmount);

        store.impose(new Sum(cardList, totalCard));
    }

}
