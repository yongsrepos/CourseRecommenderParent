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


import org.jacop.constraints.XmulCeqZ;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.IntervalDomain;
import org.jacop.core.Store;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.constraint.api.Solver;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public abstract class AbstractCreditsConstraint {

    public static IntVar getScaledCredits(Store store, CourseCredit sharedCredit, IntVar setCardVar) {

        int scaledUnitCredit = (int) (sharedCredit.getCredit() * Solver.CREDIT_NORMALIZATION_SCALE);

        IntDomain domain = new IntervalDomain(0, scaledUnitCredit * setCardVar.max());
        IntVar credits = new IntVar(store, domain);

        store.impose(new XmulCeqZ(setCardVar, scaledUnitCredit, credits));

        return credits;
    }
}
