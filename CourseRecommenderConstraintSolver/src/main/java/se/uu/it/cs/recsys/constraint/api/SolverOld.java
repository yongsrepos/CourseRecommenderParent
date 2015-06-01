//package se.uu.it.cs.recsys.constraint.api;
//
///*
// * #%L
// * CourseRecommenderConstraintSolver
// * %%
// * Copyright (C) 2015 Yong Huang  <yong.e.huang@gmail.com >
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import org.jacop.core.Domain;
//import org.jacop.core.Store;
//import org.jacop.search.DepthFirstSearch;
//import org.jacop.search.Search;
//import org.jacop.search.SelectChoicePoint;
//import org.jacop.search.SimpleSelect;
//import org.jacop.search.SimpleSolutionListener;
//import org.jacop.set.core.SetDomain;
//import org.jacop.set.core.SetVar;
//import org.jacop.set.search.IndomainSetMax;
//import org.jacop.set.search.MaxCardDiff;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import se.uu.it.cs.recsys.api.type.CourseSchedule;
//import se.uu.it.cs.recsys.constraint.builder.ScheduleDomainBuilder;
//import se.uu.it.cs.recsys.constraint.util.ConstraintResultConverter;
//import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
//
///**
// *
// * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
// */
//@Service
//public class SolverOld {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(SolverOld.class);
//
//    public static final int TOTAL_PLAN_PERIODS = 6;
//
//    private int suggestionAmount = ConstraintSolverPreference.RECOMMENDATION_AMOUNT_DEFAULT;
//
//    @Autowired
//    private CourseRepository courseRepository;
//
//    @Autowired
//    private ScheduleDomainBuilder scheduleDomainBuilder;
//
//    @Autowired
//    private ConstraintResultConverter resultConverter;
//
//    @Autowired
//    private Modeler modeler;
//
//    /**
//     * This value is use to multiply the original credit value, so that it can
//     * turn the float value into a integer one, for that to use constraint on
//     * integers.
//     */
//    public static final int CREDIT_NORMALIZATION_SCALE = 10;
//
//    public static final int CREDIT_STEP_AFTER_SCALING = (int) (0.5 * CREDIT_NORMALIZATION_SCALE);
//
//    /**
//     *
//     * @return a random course selection suggestion that matches a degree
//     * requirements
//     */
//    public List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> getRandomSolution() {
//
//        return getSolutionWithInterestedCourseIdCollection(this.courseRepository.findAllDistinctAutoGenId());
//
//    }
//
//    public Set<Set<se.uu.it.cs.recsys.api.type.Course>> getSolutionWithPreference(ConstraintSolverPreference pref) {
//
//        if (pref == null) {
//            List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> r = getRandomSolution();
//
//            Set<Set<se.uu.it.cs.recsys.api.type.Course>> totalSolution = new HashSet<>();
//
//            r.forEach(map -> {
//                Set<se.uu.it.cs.recsys.api.type.Course> singleSolution = new HashSet<>();
//
//                map.entrySet().stream()
//                        .map(entry -> entry.getValue())
//                        .forEach(set -> singleSolution.addAll(set));
//
//                totalSolution.add(singleSolution);
//            });
//
//            return totalSolution;
//        }
//        return null;
//
//    }
//
//    /**
//     *
//     * @param argInterestedCourseIdSet, non-empty input
//     * @return a course selection suggestion that matches a degree requirements,
//     * with candidates from the input; or an empty array if no satisfactory
//     * solution found
//     * @throws IllegalArgumentException if input is null or empty
//     */
//    public List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>>
//            getSolutionWithInterestedCourseIdCollection(Set<Integer> argInterestedCourseIdSet) {
//
//        if (argInterestedCourseIdSet == null || argInterestedCourseIdSet.isEmpty()) {
//            throw new IllegalArgumentException("Input must be non-empty!");
//        }
//
//        Store store = new Store();
//
//        SetVar[] pers = initSetVars(store, argInterestedCourseIdSet);
//
////        this.modeler.setScheduleInfo(getDefaultPeriodsScheduleInfo());
////        this.modeler.postConstraints(store, pers, argInterestedCourseIdSet);
//
//        return searchToMap(store, pers);
//    }
//
//    // 1. Init each period as SetVar
//    private SetVar[] initSetVars(Store store, Set<Integer> interestedCourseIdSet) {
//        LOGGER.debug("Initiate var for each study period.");
//
//        SetVar[] p = new SetVar[6];
//
//        Map<Integer, SetDomain> domains = this.scheduleDomainBuilder
//                .createScheduleSetDomainsFor(
//                        interestedCourseIdSet,
//                        getDefaultPeriodsScheduleInfo());
//
//        for (int i = 0; i <= 5; i++) {
//            p[i] = new SetVar(store, "Period_" + (i + 1), domains.get(i + 1));
//        }
//
//        return p;
//    }
//
//    public static Set<CourseSchedule> getDefaultPeriodsScheduleInfo() {
//        return Stream.of(new CourseSchedule((short) 2015, (short) 1, (short) 1),
//                new CourseSchedule((short) 2015, (short) 2, (short) 2),
//                new CourseSchedule((short) 2016, (short) 3, (short) 3),
//                new CourseSchedule((short) 2016, (short) 4, (short) 4),
//                new CourseSchedule((short) 2016, (short) 1, (short) 5),
//                new CourseSchedule((short) 2016, (short) 2, (short) 6))
//                .collect(Collectors.toSet());
//    }
//
//    private List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> searchToMap(Store store, SetVar[] vars) {
//        LOGGER.debug("Start searching solution ... ");
//
//        Domain[][] solutions = doSearch(store, vars);
//
//        if (solutions == null || solutions.length == 0) {
//            LOGGER.debug("No solution found!");
//            return Collections.EMPTY_LIST;
//        }
//
//        List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> result = new ArrayList<>();
//
//        Arrays.stream(solutions)
//                .filter(solution -> {
//                    return solution != null && solution.length > 0;
//                })
//                .forEach(solution -> {
//                    result.add(this.resultConverter.convertToMap(solution));
//                });
//
//        return result;
//
//    }
//
//    private Set<Set<se.uu.it.cs.recsys.api.type.Course>> searchToSet(Store store, SetVar[] vars, ConstraintSolverPreference pref) {
//        LOGGER.debug("Start searching solution ... ");
//
//        Domain[][] solutions = doSearch(store, vars);
//
//        if (solutions == null || solutions.length == 0) {
//            LOGGER.debug("No solution found!");
//            return Collections.emptySet();
//        }
//
//        Set<Set<se.uu.it.cs.recsys.api.type.Course>> result = new HashSet<>();
//
//        Arrays.stream(solutions)
//                .filter(solution -> {
//                    return solution != null && solution.length > 0;
//                })
//                .forEach(solution -> {
//                    result.add(this.resultConverter.convertToSet(solution));
//                });
//
//        return result;
//
//    }
//
//    private Domain[][] doSearch(Store store, SetVar[] vars) {
//        LOGGER.debug("Store constraints num: {}, consistency: {}", store.numberConstraints(), store.consistency());
//
//        Search<SetVar> search = new DepthFirstSearch<>();
//
//        SelectChoicePoint<SetVar> select = new SimpleSelect<>(
//                vars,
//                new MaxCardDiff<>(),
//                new IndomainSetMax<>()
//        );
//        search.setSolutionListener(new SimpleSolutionListener<>());
//        search.getSolutionListener().setSolutionLimit(this.suggestionAmount);
//
//        search.getSolutionListener().recordSolutions(true);
//
//        search.labeling(store, select);
//
//        LOGGER.debug("Wrong decisions: {}", search.getWrongDecisions());
//
//        return search.getSolutionListener().getSolutions();
//    }
//
//    /**
//     * A suggestion covers course selection for all study periods.
//     *
//     * @param suggestionAmount must be positive, specified how many of such
//     * suggestions will be generated.If not specified, a default value of
//     * {@link #SUGGESTION_AMOUNT_DEFAULT} will be applied.
//     *
//     * @throws IllegalArgumentException if input is less than one.
//     */
//    public void setSuggestionAmount(int suggestionAmount) {
//        if (suggestionAmount < 1) {
//            throw new IllegalArgumentException("Input must be bigger than 0!");
//        }
//
//        this.suggestionAmount = suggestionAmount;
//    }
//
////    public void addMandatoryCourse(Set<se.uu.it.cs.recsys.api.type.Course> mandatoryCourseSet) {
////        if (mandatoryCourseSet == null || mandatoryCourseSet.isEmpty()) {
////            LOGGER.info("No mandatory selection set.");
////            return;
////        }
////
////        this.modeler.addMandatoryCourse(mandatoryCourseSet);
////    }
////
////    public void addMandatoryPlan(Map<Integer, Set<Integer>> periodOneBasedIdxToMustHaveIdSet) {
////
////        this.modeler.addMandatoryPlan(periodOneBasedIdxToMustHaveIdSet);
////
////    }
//}
