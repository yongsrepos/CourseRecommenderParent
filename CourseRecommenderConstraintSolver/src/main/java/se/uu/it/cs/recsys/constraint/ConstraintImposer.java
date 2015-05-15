package se.uu.it.cs.recsys.constraint;

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jacop.constraints.Sum;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Interval;
import org.jacop.core.Store;
import org.jacop.set.constraints.AdisjointB;
import org.jacop.set.constraints.AintersectBeqC;
import org.jacop.set.constraints.AunionBeqC;
import org.jacop.set.constraints.CardA;
import org.jacop.set.constraints.CardAeqX;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import static se.uu.it.cs.recsys.constraint.Solver.MAX_COURSE_AMOUNT_PERIOD;
import static se.uu.it.cs.recsys.constraint.Solver.MIN_ALL_PERIODS_CREDIT;
import static se.uu.it.cs.recsys.constraint.Solver.MIN_COURSE_AMOUNT_PERIOD;
import se.uu.it.cs.recsys.constraint.builder.DomainBuilder;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
class ConstraintImposer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintImposer.class);

    // 2. Post disjoint const between P1 and P5, P2 and P6
    public static boolean postDisjointConst(Store store, SetVar[] vars) {
        LOGGER.debug("Posting disjoint constraint for 1st <=> 5th, 2nd <=> 6th periods.");
        
        AdisjointB disjoint1 = new AdisjointB(vars[0], vars[4]);
        AdisjointB disjoint2 = new AdisjointB(vars[1], vars[5]);
        
        store.impose(disjoint1);
        store.impose(disjoint2);
        
        LOGGER.debug("After posting constraint, is store still consistent? {}", store.consistency());
        
        return store.consistency();
    }
    
    public static boolean postTotalCreditsConst(Store store, SetVar allIdUnion, Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {
        LOGGER.debug("Posting constraints on total credits!");
        
        Map<CourseCredit, SetVar> creditToAllIdUnionSetVar = getIntersection(store, allIdUnion, creditToInterestedCourseIds);
        
        Map<CourseCredit, Integer> creditToScaledUpperLimit = getCreditToScaledUpperLimit(creditToInterestedCourseIds);
        
        IntVar totalCredits = getTotalScaledCreditsForAllPeriods(store, creditToAllIdUnionSetVar, creditToScaledUpperLimit);
        
        store.impose(new XgteqC(totalCredits, MIN_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE));
        
        LOGGER.debug("After posting total credits constraint, is store still consistent?{}", store.consistency());
        
        return store.consistency();
    }
    
    public static boolean postAdvancedCreditsConst(Store store, SetVar allIdUnion,
            Map<CourseCredit, Set<Integer>> creditToInterestedAdvancedCourseIdSetMapping) {
        LOGGER.debug("Posting constraints on advanced credits!");
        Set<Integer> allInterestedAdvancedId = creditToInterestedAdvancedCourseIdSetMapping.entrySet()
                .stream().flatMap(entry
                        -> entry.getValue().stream()
                ).collect(Collectors.toSet());
        
        SetVar advancedIdSetVar = getIntersection(store, allIdUnion, allInterestedAdvancedId);
        
        postCreditsConst(store, advancedIdSetVar, creditToInterestedAdvancedCourseIdSetMapping,
                Solver.MIN_ADVANCED_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        
        LOGGER.debug("After posting advanced credits constraint, is store still consistent?{}", store.consistency());
        return store.consistency();
    }

    // 3. Cardinality const on each period [1, 3]
    public static boolean postEachPeriodCardinalityConst(Store store, SetVar[] vars) {
        LOGGER.debug("Posting cardinality constraint for each study period.");
        
        for (SetVar var : vars) {
            CardA cardConstraint = new CardA(var, MIN_COURSE_AMOUNT_PERIOD, MAX_COURSE_AMOUNT_PERIOD);
            store.impose(cardConstraint);
        }
        
        LOGGER.debug("After posting constraint, is store still consistent? {}", store.consistency());
        return store.consistency();
        
    }
    
    public static boolean postEachPeriodCreditConst(Store store, SetVar[] periodVars, Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {
        LOGGER.debug("Posting constraints on single period credits!");
        
        for (SetVar singlePeriodVar : periodVars) {
            Map<CourseCredit, SetVar> creditToSinglePeriodIdSetVar = getIntersection(store, singlePeriodVar, creditToInterestedCourseIds);
            
            Map<CourseCredit, Integer> creditToScaledUpperLimit = getCreditToScaledUpperLimitForSinglePeriod(creditToInterestedCourseIds);
            
            IntVar totalSinglePeriodCreidt = getTotalScaledCreditsForSinglePeriod(store, creditToSinglePeriodIdSetVar, creditToScaledUpperLimit);
            
            store.impose(new XgteqC(totalSinglePeriodCreidt, Solver.MIN_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE));
            store.impose(new XlteqC(totalSinglePeriodCreidt, Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE));
        }
        
        LOGGER.debug("After posting single period credits constraint, is store still consistent?{}", store.consistency());
        return store.consistency();
    }
    
    private static void postCreditsConst(Store store, SetVar varFromSubset, Map<CourseCredit, Set<Integer>> creditToSuperset, int threshold) {
        Map<CourseCredit, SetVar> creditToInersection = getIntersection(store, varFromSubset, creditToSuperset);
        
        Map<CourseCredit, Integer> creditToScaledUpperLimit = getCreditToScaledUpperLimit(creditToSuperset);
        
        IntVar totalCredits = getTotalScaledCreditsForAllPeriodsAdvancedCourses(store, creditToInersection, creditToScaledUpperLimit);
        
        store.impose(new XgteqC(totalCredits, threshold));
    }
    
    public static SetVar getCourseIdUnion(Store store, SetVar[] periodCourseIdVars, Set<Integer> allInterestedCourseIds) {
        SetVar union = periodCourseIdVars[0];
        
        for (int i = 1; i < periodCourseIdVars.length; i++) {
            SetVar partUnion = new SetVar(store, "union_" + i,
                    DomainBuilder.createDomain(allInterestedCourseIds));
            store.impose(new AunionBeqC(union, periodCourseIdVars[i], partUnion));
            union = partUnion;
            i++;
        }
        
        return union;
    }
    
    public static Map<CourseCredit, SetVar> getIntersection(Store store,
            SetVar candidate,
            Map<CourseCredit, Set<Integer>> creditAndValidDomainMap) {
        
        return creditAndValidDomainMap
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    return getIntersection(store, candidate,
                                            entry.getValue());
                                }));
        
    }

    /**
     *
     * @param store
     * @param idSet
     * @param intersectWith
     * @return the intersection set var, ranging from empty to the domain
     * containing all elements from the set of input elements (intersectWith)
     */
    public static SetVar getIntersection(Store store, SetVar idSet, Set<Integer> intersectWith) {
        SetVar intersectWithVar = new SetVar(store, DomainBuilder.createDomain(intersectWith));
        
        SetDomain resultDomain = new BoundSetDomain(IntDomain.emptyIntDomain,
                intersectWithVar.dom().lub());
        SetVar resultIntersectionVar = new SetVar(store, resultDomain);
        
        store.impose(new AintersectBeqC(idSet, intersectWithVar, resultIntersectionVar));
        
        return resultIntersectionVar;
    }
    
    public static Map<CourseCredit, Integer> getCreditToScaledUpperLimit(Map<CourseCredit, Set<Integer>> creditToInterestedIdSet) {
        return creditToInterestedIdSet.entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getValue().size() > 0;
                })
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    int scaledCreditUnit = (int) (entry.getKey().getCredit()
                                    * Solver.CREDIT_NORMALIZATION_SCALE);
                                    return entry.getValue().size() * scaledCreditUnit;
                                }));
    }
    
    public static Map<CourseCredit, Integer> getCreditToScaledUpperLimitForSinglePeriod(Map<CourseCredit, Set<Integer>> creditToInterestedIdSet) {
        return creditToInterestedIdSet.entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getValue().size() > 0;
                })
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    int scaledCreditUnit = (int) (entry.getKey().getCredit()
                                    * Solver.CREDIT_NORMALIZATION_SCALE);
                                    
                                    int upperLimit = entry.getValue().size() * scaledCreditUnit > Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE
                                            ? Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE
                                            : entry.getValue().size() * scaledCreditUnit;
                                    return upperLimit;
                                }));
    }
    
    private static IntVar getTotalScaledCreditsForAllPeriods(Store store, Map<CourseCredit, SetVar> creditToAllIdUnionSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {
        
        List<IntVar> creditsList = creditToAllIdUnionSetVar.entrySet()
                .stream().map(entry -> {
                    Interval singleCreditsDomainInterval = new Interval(0, creditToScaledUpperLimit.get(entry.getKey()));
                    
                    IntDomain singleCreditsDomain = DomainBuilder.createIntDomain(singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);
                    
                    return getScaledCredits(store, entry.getKey(), entry.getValue(), singleCreditsDomain);
                })
                .collect(Collectors.toList());
        
        ArrayList<IntVar> creditsArrayList = new ArrayList<>();
        creditsArrayList.addAll(creditsList);
        
        Interval totalCreditsInterval = new Interval(Solver.MIN_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                Solver.MAX_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        IntVar totalCreditsVar = new IntVar(store, DomainBuilder.createIntDomain(totalCreditsInterval, Solver.CREDIT_STEP_AFTER_SCALING));
        
        store.impose(new Sum(creditsArrayList, totalCreditsVar));
        
        return totalCreditsVar;
        
    }
    
    private static IntVar getTotalScaledCreditsForAllPeriodsAdvancedCourses(Store store,
            Map<CourseCredit, SetVar> creditToAllAdvancedCourseIdSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {
        
        List<IntVar> creditsList = creditToAllAdvancedCourseIdSetVar.entrySet()
                .stream().map(entry -> {
                    Interval singleCreditsDomainInterval = new Interval(0, creditToScaledUpperLimit.get(entry.getKey()));
                    
                    IntDomain singleCreditsDomain = DomainBuilder.createIntDomain(singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);
                    
                    return getScaledCredits(store, entry.getKey(), entry.getValue(), singleCreditsDomain);
                })
                .collect(Collectors.toList());
        
        ArrayList<IntVar> creditsArrayList = new ArrayList<>();
        creditsArrayList.addAll(creditsList);
        
        Interval totalAdvancedCreditsInterval = new Interval(Solver.MIN_ADVANCED_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                Solver.MAX_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        
        IntVar totalAdvancedCreditsVar = new IntVar(store,
                DomainBuilder.createIntDomain(totalAdvancedCreditsInterval,
                        Solver.CREDIT_STEP_AFTER_SCALING));
        
        store.impose(new Sum(creditsArrayList, totalAdvancedCreditsVar));
        
        return totalAdvancedCreditsVar;
        
    }
    
    private static IntVar getTotalScaledCreditsForSinglePeriod(Store store, Map<CourseCredit, SetVar> creditToSinglePeriodSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {
        
        List<IntVar> creditsList = creditToSinglePeriodSetVar.entrySet()
                .stream().map(entry -> {
                    Interval singleCreditsDomainInterval = new Interval(0, creditToScaledUpperLimit.get(entry.getKey()));
                    
                    IntDomain singleCreditsDomain = DomainBuilder.createIntDomain(singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);
                    
                    return getScaledCredits(store, entry.getKey(), entry.getValue(), singleCreditsDomain);
                })
                .collect(Collectors.toList());
        
        ArrayList<IntVar> creditsArrayList = new ArrayList<>();
        creditsArrayList.addAll(creditsList);
        
        Interval totalSinglePeriodCreditsInterval = new Interval(Solver.MIN_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        IntVar totalCreditsVar = new IntVar(store, DomainBuilder.createIntDomain(totalSinglePeriodCreditsInterval, Solver.CREDIT_STEP_AFTER_SCALING));
        
        store.impose(new Sum(creditsArrayList, totalCreditsVar));
        
        return totalCreditsVar;
        
    }

    /**
     *
     * @param store
     * @param idSetVar
     * @param sharedCreditByAllIdFromTheSet
     * @param creditsDomain
     * @return Card(idSetVar) * credit
     */
    private static IntVar getScaledCredits(Store store, CourseCredit sharedCreditByAllIdFromTheSet, SetVar idSetVar, IntDomain creditsDomain) {
        
        IntVar card = getCardinality(store, idSetVar);
        
        int scaledUnitCredit = (int) (sharedCreditByAllIdFromTheSet.getCredit() * Solver.CREDIT_NORMALIZATION_SCALE);
        
        IntVar credits = new IntVar(store, creditsDomain);
        
        store.impose(new XmulCeqZ(card, scaledUnitCredit, credits));
        
        return credits;
    }
    
    private static IntVar getCardinality(Store store, SetVar var) {
        IntVar card = new IntVar(store, 0, var.dom().getSize());
        
        store.impose(new CardAeqX(var, card));
        
        return card;
    }
    
}
