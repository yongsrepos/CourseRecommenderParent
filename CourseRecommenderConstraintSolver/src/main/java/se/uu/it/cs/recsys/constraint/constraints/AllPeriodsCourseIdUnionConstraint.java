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
import java.util.Collections;
import java.util.Set;
import org.jacop.core.Store;
import org.jacop.set.constraints.AunionBeqC;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class AllPeriodsCourseIdUnionConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllPeriodsCourseIdUnionConstraint.class);

    public static SetVar imposeAndGetUnion(
            Store store, 
            SetVar[] periodCourseIdVars, 
            Set<Integer> interestedCourseIdSet) {
        
        LOGGER.debug("Posting constraints on the union of all course ids.");

        if (periodCourseIdVars == null || periodCourseIdVars.length == 0) {
            throw new IllegalArgumentException("Array must be non-empty!");
        }
        
        int minCourseId = Collections.min(interestedCourseIdSet);
        int maxCourseId = Collections.max(interestedCourseIdSet);
        
        SetVar union = periodCourseIdVars[0];

        for (int i = 1; i < periodCourseIdVars.length; i++) {
            SetVar partUnion
                    = new SetVar(store,
                            "part_union_" + i,
                            new BoundSetDomain(minCourseId, maxCourseId));

            store.impose(new AunionBeqC(union, periodCourseIdVars[i], partUnion));
            union = partUnion;
        }
        
        return union;
    }

}
