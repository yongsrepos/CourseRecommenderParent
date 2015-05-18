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
import java.util.Collections;
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
import org.jacop.set.constraints.AintersectBeqC;
import org.jacop.set.constraints.AunionBeqC;
import org.jacop.set.constraints.CardA;
import org.jacop.set.constraints.EinA;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.constraint.api.Solver;
import static se.uu.it.cs.recsys.constraint.api.Solver.MAX_COURSE_AMOUNT_PERIOD;
import static se.uu.it.cs.recsys.constraint.api.Solver.MIN_COURSE_AMOUNT_PERIOD;
import se.uu.it.cs.recsys.constraint.builder.DomainBuilder;
import se.uu.it.cs.recsys.constraint.constraints.TotalCreditsConstraint;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class ConstraintImposer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintImposer.class);



    public static void postAdvancedCreditsConst(Store store, SetVar allIdUnion,
            Map<CourseCredit, Set<Integer>> creditToInterestedAdvancedCourseIdSetMapping) {
        LOGGER.debug("Posting constraints on advanced credits!");
        Set<Integer> allInterestedAdvancedId
                = creditToInterestedAdvancedCourseIdSetMapping.entrySet()
                .stream()
                .flatMap(entry
                        -> entry.getValue().stream()
                )
                .collect(Collectors.toSet());

        SetVar advancedIdSetVar = getIntersection(store, allIdUnion,
                allInterestedAdvancedId,
                new Interval(0, allInterestedAdvancedId.size()));

        postAdvancedCreditsConst(store,
                advancedIdSetVar,
                creditToInterestedAdvancedCourseIdSetMapping,
                Solver.MIN_ADVANCED_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
    }


    public static void postEachPeriodCreditConst(Store store,
            SetVar[] periodVars,
            Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {

        for (SetVar singlePeriodVar : periodVars) {

            LOGGER.debug("Posting constraints on {} credits!", singlePeriodVar.id());

            Map<CourseCredit, SetVar> creditToSinglePeriodIdSetVar
                    = getIntersection(store,
                            singlePeriodVar,
                            creditToInterestedCourseIds);

            Map<CourseCredit, Integer> creditToScaledUpperLimit
                    = getCreditToScaledUpperLimitForSinglePeriod(creditToInterestedCourseIds);

            IntVar totalSinglePeriodCreidt
                    = getTotalScaledCreditsForSinglePeriod(store,
                            creditToSinglePeriodIdSetVar,
                            creditToScaledUpperLimit);

            XgteqC minCreditsConst
                    = new XgteqC(totalSinglePeriodCreidt,
                            Solver.MIN_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
            store.impose(minCreditsConst);

            XlteqC maxCredtisConst
                    = new XlteqC(totalSinglePeriodCreidt,
                            Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
            store.impose(maxCredtisConst);
        }
    }

    private static void postAdvancedCreditsConst(Store store,
            SetVar varFromSubset,
            Map<CourseCredit, Set<Integer>> creditToSuperset, int threshold) {

        Map<CourseCredit, SetVar> creditToInersection
                = getIntersection(store, varFromSubset, creditToSuperset);

        Map<CourseCredit, Integer> creditToScaledUpperLimit
                = getCreditToScaledUpperLimit(creditToSuperset);

        IntVar totalCredits = getTotalScaledCreditsForAllAdvancedCourses(
                store,
                creditToInersection,
                creditToScaledUpperLimit);

        XgteqC thresholdConst = new XgteqC(totalCredits, threshold);
        store.impose(thresholdConst);
    }


    public static Map<CourseCredit, SetVar> getIntersection(Store store,
            SetVar candidate,
            Map<CourseCredit, Set<Integer>> creditAndValidDomainMap) {

        return creditAndValidDomainMap
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    return getIntersection(store,
                                            candidate,
                                            entry.getValue(),
                                            new Interval(0, entry.getValue().size()));
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
    public static SetVar getIntersection(Store store, SetVar idSet, Set<Integer> intersectWith,
            Interval intersectonCardInterval) {

        SetVar intersectWithVar = new SetVar(store, DomainBuilder.createDomain(intersectWith));

        SetDomain resultDomain = new BoundSetDomain(IntDomain.emptyIntDomain,
                intersectWithVar.dom().lub());

        SetVar resultIntersectionVar = new SetVar(store, resultDomain);

        store.impose(new AintersectBeqC(idSet, intersectWithVar, resultIntersectionVar));

        store.impose(new CardA(resultIntersectionVar,
                intersectonCardInterval.min(),
                intersectonCardInterval.max()));

        return resultIntersectionVar;
    }

    public static Map<CourseCredit, Integer> getCreditToScaledUpperLimit(
            Map<CourseCredit, Set<Integer>> creditToInterestedIdSet) {

        return creditToInterestedIdSet.entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getValue().size() > 0;
                })
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    int scaledCreditUnit
                                    = (int) (entry.getKey().getCredit()
                                    * Solver.CREDIT_NORMALIZATION_SCALE);

                                    return entry.getValue().size() * scaledCreditUnit;
                                }));
    }

    public static Map<CourseCredit, Integer> getCreditToScaledUpperLimitForSinglePeriod(
            Map<CourseCredit, Set<Integer>> creditToInterestedIdSet) {

        return creditToInterestedIdSet.entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getValue().size() > 0;
                })
                .collect(Collectors.toMap(Map.Entry::getKey,
                                entry -> {
                                    int scaledCreditUnit = (int) (entry.getKey().getCredit()
                                    * Solver.CREDIT_NORMALIZATION_SCALE);

                                    boolean exceedsMaxPeriodAmount = entry.getValue().size()
                                    * scaledCreditUnit > Solver.MAX_PERIOD_CREDIT
                                    * Solver.CREDIT_NORMALIZATION_SCALE;

                                    int upperLimit = exceedsMaxPeriodAmount
                                            ? Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE
                                            : entry.getValue().size() * scaledCreditUnit;

                                    return upperLimit;
                                }));
    }


    private static IntVar getTotalScaledCreditsForAllAdvancedCourses(Store store,
            Map<CourseCredit, SetVar> creditToAllAdvancedCourseIdSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {

        List<IntVar> creditsList = creditToAllAdvancedCourseIdSetVar.entrySet()
                .stream()
                .map(entry -> {
                    Interval singleCreditsDomainInterval
                    = new Interval(0, creditToScaledUpperLimit.get(entry.getKey()));

                    IntDomain singleCreditsDomain
                    = DomainBuilder.createIntDomain(
                            singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);

                    return TotalCreditsConstraint.getScaledCredits(store,
                            entry.getKey(),
                            entry.getValue(),
                            singleCreditsDomain);
                })
                .collect(Collectors.toList());

        ArrayList<IntVar> creditsArrayList = new ArrayList<>(creditsList);

        Interval totalAdvancedCreditsInterval
                = new Interval(
                        Solver.MIN_ADVANCED_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                        Solver.MAX_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);

        IntVar totalAdvancedCreditsVar
                = new IntVar(store,
                        DomainBuilder.createIntDomain(
                                totalAdvancedCreditsInterval,
                                Solver.CREDIT_STEP_AFTER_SCALING));

        store.impose(new Sum(creditsArrayList, totalAdvancedCreditsVar));

        return totalAdvancedCreditsVar;

    }

    private static IntVar getTotalScaledCreditsForSinglePeriod(Store store,
            Map<CourseCredit, SetVar> creditToSinglePeriodSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {

        List<IntVar> creditsList = creditToSinglePeriodSetVar.entrySet()
                .stream()
                .map(entry -> {
                    Interval singleCreditsDomainInterval
                    = new Interval(0,
                            creditToScaledUpperLimit.get(entry.getKey()));

                    IntDomain singleCreditsDomain
                    = DomainBuilder.createIntDomain(
                            singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);

                    return TotalCreditsConstraint.getScaledCredits(store,
                            entry.getKey(),
                            entry.getValue(),
                            singleCreditsDomain);
                })
                .collect(Collectors.toList());

        ArrayList<IntVar> creditsArrayList = new ArrayList<>();
        creditsArrayList.addAll(creditsList);

        Interval totalSinglePeriodCreditsInterval
                = new Interval(
                        Solver.MIN_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                        Solver.MAX_PERIOD_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);

        IntVar totalCreditsVar
                = new IntVar(
                        store,
                        DomainBuilder.createIntDomain(
                                totalSinglePeriodCreditsInterval,
                                Solver.CREDIT_STEP_AFTER_SCALING));

        store.impose(new Sum(creditsArrayList, totalCreditsVar));

        return totalCreditsVar;

    }


}
