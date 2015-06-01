package se.uu.it.cs.recsys.constraint.solver;

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
import org.jacop.core.Store;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.constraint.constraints.AdvancedCreditsConstraint;
import se.uu.it.cs.recsys.constraint.constraints.AllPeriodsCourseIdUnionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.AvoidSameCourseConstraint;
import se.uu.it.cs.recsys.constraint.constraints.FixedCourseSelectionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.SinglePeriodCourseCardinalityConstraint;
import se.uu.it.cs.recsys.constraint.constraints.SingleStudyPeriodCreditsConstraint;
import se.uu.it.cs.recsys.constraint.constraints.TotalCourseCardinalityConstraint;
import se.uu.it.cs.recsys.constraint.constraints.TotalCreditsConstraint;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class Modeler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Modeler.class);

    private ModelConfig config;

    public void postConstraints(Store store, SetVar[] pers,
            ModelConfig config) {

        LOGGER.debug("Posting constraints!");

        this.config = config;

        //1. on cardinality
        SinglePeriodCourseCardinalityConstraint.impose(store, pers);
        TotalCourseCardinalityConstraint.impose(
                store,
                pers,
                config.getMaxCourseAmount());

        //2. on the must-have ones
        FixedCourseSelectionConstraint.impose(
                store,
                pers,
                this.config.getPeriodIdxToMustHaveCourseId());

        SetVar allCourseIdUnion = AllPeriodsCourseIdUnionConstraint
                .imposeAndGetUnion(
                        store,
                        pers,
                        this.config.getInterestedCourseIdCollection());

        //3. avoid selecting same course
        AvoidSameCourseConstraint.impose(
                store,
                allCourseIdUnion,
                this.config.getAvoidCollectionForCourseWithDiffIdSet());

        //4. on credits
        AdvancedCreditsConstraint.impose(
                store,
                allCourseIdUnion,
                this.config.getCreditToAdvancedId(),
                this.config.getMaxAdvancedCredits());

        SingleStudyPeriodCreditsConstraint.impose(
                store,
                pers,
                this.config.getCreditToCourseId());

        TotalCreditsConstraint.impose(store,
                allCourseIdUnion,
                this.config.getCreditToCourseId(),
                this.config.getMaxTotalCredits());
    }
}
