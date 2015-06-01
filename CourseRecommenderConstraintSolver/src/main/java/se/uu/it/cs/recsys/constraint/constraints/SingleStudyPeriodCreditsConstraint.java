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
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.jacop.constraints.Sum;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;
import static se.uu.it.cs.recsys.constraint.constraints.AbstractCreditsConstraint.getScaledCredits;
import se.uu.it.cs.recsys.constraint.solver.Solver;
import se.uu.it.cs.recsys.constraint.util.Util;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class SingleStudyPeriodCreditsConstraint extends AbstractCreditsConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleStudyPeriodCreditsConstraint.class);

    public static void impose(Store store, SetVar[] periodVars, Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {

        LOGGER.debug("Posting constraints on single study period credits!");

        Arrays.stream(periodVars)
                .forEach(periodVar -> {

                    imposeForSinglePeriod(store, periodVar, creditToInterestedCourseIds);

                });
    }

    private static void imposeForSinglePeriod(Store store, SetVar singlePeriodVar, 
            Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {

        ArrayList<IntVar> creditVarListForEachCreditType = new ArrayList<>();

        creditToInterestedCourseIds.forEach((credit, idSetOnSameCredit) -> {
            IntVar intersectionCardVar = Util.getIntersectionCardVar(store,
                    singlePeriodVar, idSetOnSameCredit);

            IntVar creditsForCurrentCreditType = getScaledCredits(store,
                    credit, intersectionCardVar);

            creditVarListForEachCreditType.add(creditsForCurrentCreditType);
        });

        IntVar totalCredits = new IntVar(store,
                (int)(ConstraintSolverPreference.MIN_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE),
                (int)(ConstraintSolverPreference.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE));

        store.impose(new Sum(creditVarListForEachCreditType, totalCredits));

    }

}
