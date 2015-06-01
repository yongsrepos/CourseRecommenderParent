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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jacop.core.Domain;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SimpleSolutionListener;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;
import org.jacop.set.search.IndomainSetMax;
import org.jacop.set.search.MaxCardDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;
import se.uu.it.cs.recsys.constraint.builder.ScheduleDomainBuilder;
import se.uu.it.cs.recsys.constraint.util.ConstraintResultConverter;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Service
public class Solver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);

    @Autowired
    private ScheduleDomainBuilder scheduleDomainBuilder;

    @Autowired
    private ConstraintResultConverter resultConverter;

    @Autowired
    private Modeler modeler;

    @Autowired
    private ModelConfigBuilder modelConfigBuilder;

    private ConstraintSolverPreference pref;

    /**
     * This value is use to multiply the original credit value, so that it can
     * turn the float value into a integer one, for that to use constraint on
     * integers.
     */
    public static final int CREDIT_NORMALIZATION_SCALE = 10;

    public static final int CREDIT_STEP_AFTER_SCALING = (int) (0.5 * CREDIT_NORMALIZATION_SCALE);

    public Set<List<se.uu.it.cs.recsys.api.type.Course>> getSolution(ConstraintSolverPreference pref) {

        if (pref == null) {
            throw new IllegalArgumentException("Input must be not null!");
        }

        this.pref = pref;

        Store store = new Store();

        SetVar[] vars = initVars(store);

        ModelConfig modelConfig = this.modelConfigBuilder.buildFrom(this.pref);

        this.modeler.postConstraints(store, vars, modelConfig);

        return search(store, vars);

    }

    // 1. Init each period as SetVar
    private SetVar[] initVars(Store store) {
        LOGGER.debug("Initiate var for each study period.");

        final int TOTAL_PLAN_PERIODS = this.pref.getIndexedScheduleInfo().size();

        SetVar[] vars = new SetVar[TOTAL_PLAN_PERIODS];

        Map<Integer, SetDomain> domains = this.scheduleDomainBuilder
                .createScheduleSetDomainsFor(
                        this.pref.getInterestedCourseIdCollection(),
                        this.pref.getIndexedScheduleInfo());

        for (int i = 0; i <= TOTAL_PLAN_PERIODS - 1; i++) {
            vars[i] = new SetVar(store, "Period_" + (i + 1), domains.get(i + 1));
        }

        return vars;
    }

    private Set<List<se.uu.it.cs.recsys.api.type.Course>> search(Store store, SetVar[] vars) {
        LOGGER.debug("Start searching solution ... ");

        Domain[][] solutions = doSearch(store, vars);

        if (solutions == null || solutions.length == 0) {
            LOGGER.debug("No solution found!");
            return Collections.emptySet();
        }

        Set<List<se.uu.it.cs.recsys.api.type.Course>> result = new HashSet<>();

        Arrays.stream(solutions)
                .filter(solution -> {
                    return solution != null && solution.length > 0;
                })
                .forEach(solution -> {
                    result.add(this.resultConverter.convert(solution));
                });

        return result;

    }

    private Domain[][] doSearch(Store store, SetVar[] vars) {

        LOGGER.debug("Store constraints num: {}", store.numberConstraints());

        if (!store.consistency()) {
            LOGGER.debug("Store is not consistency. No solution!");
            return new Domain[0][0];
        }
        
        LOGGER.debug("Searching solution ....");

        Search<SetVar> search = new DepthFirstSearch<>();

        SelectChoicePoint<SetVar> select = new SimpleSelect<>(
                vars,
                new MaxCardDiff<>(),
                new IndomainSetMax<>()
        );

        search.setSolutionListener(new SimpleSolutionListener<>());
        search.getSolutionListener()
                .setSolutionLimit(this.pref.getRecommendationAmount());

        search.getSolutionListener().recordSolutions(true);

        search.labeling(store, select);

        return search.getSolutionListener().getSolutions();
    }

}
