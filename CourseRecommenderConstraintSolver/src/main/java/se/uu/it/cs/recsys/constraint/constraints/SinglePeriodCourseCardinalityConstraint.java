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
import org.jacop.core.Store;
import org.jacop.set.constraints.CardA;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class SinglePeriodCourseCardinalityConstraint {

    public static final int MIN_COURSE_AMOUNT_EACH_PERIOD = 1;
    public static final int MAX_COURSE_AMOUNT_EACH_PERIOD = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePeriodCourseCardinalityConstraint.class);

    public static void impose(Store store, SetVar[] periodVars) {
        LOGGER.debug("Posting cardinality constraint for each single study period.");

        for (SetVar var : periodVars) {
            CardA cardConstraint = new CardA(var, 
                    MIN_COURSE_AMOUNT_EACH_PERIOD,
                    MAX_COURSE_AMOUNT_EACH_PERIOD);
            
            store.impose(cardConstraint);
        }
    }

}
