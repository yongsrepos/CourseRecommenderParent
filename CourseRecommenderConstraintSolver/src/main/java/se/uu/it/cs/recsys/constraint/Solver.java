
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
import java.util.Map;
import java.util.Set;
import org.jacop.constraints.Sum;
import org.jacop.constraints.XgteqC;
import org.jacop.constraints.XlteqC;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleSelect;
import org.jacop.search.SimpleSolutionListener;
import org.jacop.set.constraints.AdisjointB;
import org.jacop.set.constraints.AintersectBeqC;
import org.jacop.set.constraints.CardA;
import org.jacop.set.constraints.CardAeqX;
import org.jacop.set.core.SetDomain;
import org.jacop.set.core.SetVar;
import org.jacop.set.search.IndomainSetMin;
import org.jacop.set.search.MaxGlbCard;
import org.jacop.set.search.MinLubCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.constraint.builder.CreditDomainBuilder;
import se.uu.it.cs.recsys.constraint.builder.LevelDomainBuilder;
import se.uu.it.cs.recsys.constraint.builder.ScheduleDomainBuilder;

/**
 * <pre>
 * 1. SetDomains, d1...d6
 * 2. SetVars p1...p6
 * 3. p1 -> d1; p5 -> d1
 * 4. p1 disjoint p5
 * 5. Card(pi) [1, 3]
 * 6. Credit(pi) [10, 25]
 * 7. Semester credit [27.5, 35]
 * 9. Credit(all) >= 90
 * 10. SetDomain advance: D(advanced)
 * 11. SetDomain basic: D(basic)
 * 12. Credit(advanced) >= 60
 * </pre>
 */
@Service
public class Solver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);

    @Autowired
    private LevelDomainBuilder levelDomainBuilder;

    @Autowired
    private CreditDomainBuilder creditDomainBuilder;

    @Autowired
    private ScheduleDomainBuilder scheduleDomainBuilder;

    public static final int TOTAL_PLAN_PERIODS = 6;

    public static final int MIN_PERIOD_CREDIT = 10;
    public static final int MAX_PERIOD_CREDIT = 25;
    public static final int MIN_SEMESTER_CREDIT = 28;
    public static final int MAX_SEMESTER_CREDIT = 35;
    public static final int MIN_ADVANCED_CREDIT = 60;
    public static final int MIN_ALL_PERIODS_CREDIT = 90;
    public static final int MIN_COURSE_AMOUNT_PERIOD = 1;
    public static final int MAX_COURSE_AMOUNT_PERIOD = 3;

    public SetVar[] getSolution(Set<Integer> interestedCourseIdSet) {
        Store store = new Store();

        SetVar[] pers = initSetVars(store, interestedCourseIdSet);

        postDisjoint(store, pers);

        postPeriodCard(store, pers);
        
        if (store.consistency()) {
            return search(store, pers);
        }

        postPeriodCredit(store, pers, interestedCourseIdSet);

        postSemesterCredit(store, pers, interestedCourseIdSet);

        postTotalCredit(store, pers, interestedCourseIdSet);

        postCourseLevelConst(store, pers, interestedCourseIdSet);

        return search(store, pers);

    }

    private SetVar[] search(Store store, SetVar[] vars) {
        LOGGER.debug("Start searching solution ... ");

        boolean result = store.consistency();

        LOGGER.debug("Has CSP solution? {}", result);

        Search<SetVar> label = new DepthFirstSearch<>();

        SelectChoicePoint<SetVar> select = new SimpleSelect<>(vars,
                new MinLubCard<>(), new MaxGlbCard<>(),
                new IndomainSetMin<>());

        label.setSolutionListener(new SimpleSolutionListener<>());

        result = label.labeling(store, select);

        if (result) {
            LOGGER.debug("Solution: {}", result);
        } else {
            LOGGER.debug("No solution!");
            return new SetVar[0];
        }

        return vars;
    }

    // 1. Init each period as SetVar
    private SetVar[] initSetVars(Store store, Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Initiate var for each study period.");

        SetVar[] p = new SetVar[6];

        Map<Integer, SetDomain> domains = this.scheduleDomainBuilder
                .createScheduleSetDomainsFor(interestedCourseIdSet);

        for (int i = 0; i <= 5; i++) {
            p[i] = new SetVar(store, "Period_" + (i + 1), domains.get(i + 1));
        }

        return p;
    }

    // 2. Post disjoint const between P1 and P5, P2 and P6
    private void postDisjoint(Store store, SetVar[] vars) {
        LOGGER.debug("Posting disjoint constraint for 1&5, 2&6 periods.");

        AdisjointB disjoint1 = new AdisjointB(vars[0], vars[4]);
        AdisjointB disjoint2 = new AdisjointB(vars[1], vars[5]);

        store.impose(disjoint1);
        store.impose(disjoint2);

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());

    }

    // 3. Cardinality const on each period [1, 3]
    private void postPeriodCard(Store store, SetVar[] vars) {
        LOGGER.debug("Posting cardinality constraint for each study period.");

        for (SetVar var : vars) {
            CardA cardConstraint = new CardA(var, MIN_COURSE_AMOUNT_PERIOD, MAX_COURSE_AMOUNT_PERIOD);
            store.impose(cardConstraint);
        }

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());

    }

    // 4. Post credit const for each period
    private void postPeriodCredit(Store store, SetVar[] vars,
            Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Posting constraint on credits for each study period.");

        for (SetVar periodVar : vars) {
            IntVar credit = getCredits(store, periodVar, interestedCourseIdSet);

            XgteqC periodCreditMinConst = new XgteqC(credit, MIN_PERIOD_CREDIT);
            XlteqC periodCreditMaxConst = new XlteqC(credit, MAX_PERIOD_CREDIT);

            store.impose(periodCreditMinConst);
            store.impose(periodCreditMaxConst);
        }

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());

    }

    // 5. Post credit const for each semester [27.5, 35]
    private void postSemesterCredit(Store store, SetVar[] vars,
            Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Posting constraint on credits for each study semester.");

        IntVar p1 = getCredits(store, vars[0], interestedCourseIdSet);
        IntVar p2 = getCredits(store, vars[1], interestedCourseIdSet);

        // semester 1
        ArrayList<IntVar> semester1 = new ArrayList<>();
        semester1.add(p1);
        semester1.add(p2);

        IntVar semester1Credit = new IntVar(store, MIN_SEMESTER_CREDIT,
                MAX_SEMESTER_CREDIT);
        Sum semester1Const = new Sum(semester1, semester1Credit);
        store.impose(semester1Const);

        // semester 2
        IntVar p3 = getCredits(store, vars[2], interestedCourseIdSet);
        IntVar p4 = getCredits(store, vars[3], interestedCourseIdSet);
        ArrayList<IntVar> semester2 = new ArrayList<>();
        semester2.add(p3);
        semester2.add(p4);
        IntVar semester2Credit = new IntVar(store, MIN_SEMESTER_CREDIT,
                MAX_SEMESTER_CREDIT);
        Sum semester2Const = new Sum(semester2, semester2Credit);
        store.impose(semester2Const);

        // semester 3
        IntVar p5 = getCredits(store, vars[4], interestedCourseIdSet);
        IntVar p6 = getCredits(store, vars[5], interestedCourseIdSet);
        ArrayList<IntVar> semester3 = new ArrayList<>();
        semester3.add(p5);
        semester3.add(p6);
        IntVar semester3Credit = new IntVar(store, MIN_SEMESTER_CREDIT,
                MAX_SEMESTER_CREDIT);
        Sum semester3Const = new Sum(semester3, semester3Credit);
        store.impose(semester3Const);

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());

    }

    // 6. Post total credit const sum >= 90
    private void postTotalCredit(Store store, SetVar[] vars,
            Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Posting constraint on total credits for all study periods.");

        IntVar totalCredits = getTotalCredits(store, vars, interestedCourseIdSet);

        XgteqC theConst = new XgteqC(totalCredits, MIN_ALL_PERIODS_CREDIT);

        store.impose(theConst);

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());

    }

    // 7. Post level const, sum(advanced) >= 60
    private void postCourseLevelConst(Store store, SetVar[] periodVars,
            Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Posting constraint on course levels, e.g advanced course total credits.");

        IntVar advancedCredits = getAllAdvancedLevelCredits(store, periodVars,
                interestedCourseIdSet);

        XgteqC theConst = new XgteqC(advancedCredits, MIN_ADVANCED_CREDIT);

        store.impose(theConst);

        LOGGER.debug("After posting constraint, is store consistent? {}", store.consistency());
    }

    private IntVar getAllAdvancedLevelCredits(Store store, SetVar[] periods,
            Set<Integer> interestedCourseIdSet) {
        ArrayList<IntVar> creditsForEachPeriodByLevel = new ArrayList<>();

        for (SetVar period : periods) {
            IntVar credit = getPeriodCreditsByCourseLevel(store, period,
                    CourseLevel.ADVANCED, interestedCourseIdSet);
            creditsForEachPeriodByLevel.add(credit);
        }

        IntVar totalAdvancedCreditsVar = new IntVar(store, MIN_ADVANCED_CREDIT,
                MAX_PERIOD_CREDIT * TOTAL_PLAN_PERIODS);

        Sum sumConst = new Sum(creditsForEachPeriodByLevel, totalAdvancedCreditsVar);

        return sumConst.sum;
    }

    /*
     * Gets the total credits by summing up the credits from different periods.
     */
    private IntVar getTotalCredits(Store store, SetVar[] vars,
            Set<Integer> interestedCourseIdSet) {

        /*
         * Sum up the total credit for courses at different credit levels.
         */
        ArrayList<IntVar> creditsForEachPeriod = new ArrayList<>();

        for (SetVar period : vars) {
            IntVar sum = getCredits(store, period, interestedCourseIdSet);
            creditsForEachPeriod.add(sum);
        }

        IntVar totalCreditVar = new IntVar(store, MIN_ALL_PERIODS_CREDIT,
                MAX_PERIOD_CREDIT * TOTAL_PLAN_PERIODS);

        Sum sumConst = new Sum(creditsForEachPeriod, totalCreditVar);

        return sumConst.sum;
    }

    /**
     * Calculates the total credits of the input course ID set.
     *
     * @param store
     * @param idSetVar the SetVar for the id set
     * @return
     */
    private IntVar getCredits(Store store, SetVar idSetVar,
            Set<Integer> interestedCourseIdSet) {
        /*
         * Sum up the total credit for courses at different credit levels.
         */
        ArrayList<IntVar> credits = new ArrayList<>();

        for (CourseCredit credit : CourseCredit.values()) {
            IntVar sum = getCreditsByCreditCategory(store, idSetVar,
                    credit, interestedCourseIdSet);
            credits.add(sum);
        }

        IntVar periodCreditVar = new IntVar(store, MIN_PERIOD_CREDIT, MAX_PERIOD_CREDIT);

        Sum sumConst = new Sum(credits, periodCreditVar);

        return sumConst.sum;
    }

    /**
     * Gets the credits of courses at the specified level for the specified
     * period.
     *
     * @param store
     * @param idSetVar the set of candidate courses available in this period
     * @param credit the credit level of a course, e.g {@link CourseCredit#FIVE}
     * @return total credits from the courses of the input credit
     */
    private IntVar getCreditsByCreditCategory(Store store, SetVar idSetVar,
            CourseCredit credit, Set<Integer> interestedCourseIdSet) {
        /*
         * Get all courses at current period; gets the intersect with
         * eventual planned courses to select; multiply cardinality of the
         * intersect set with the credit.
         */
        Map<CourseCredit, SetDomain> creditAndDom = this.creditDomainBuilder
                .getCreditAndIdConstaintDomainMappingFor(interestedCourseIdSet);

        SetDomain domForCredit = creditAndDom.get(credit);

        if (domForCredit == null) {
            LOGGER.warn("No mapping domain for credit {}", credit.getCredit());
            return new IntVar(store, 0, 0);
        }

        SetVar courseIdSetForCredit = new SetVar(store, domForCredit);

        SetVar intersectVar = new SetVar(store, idSetVar.dom());

        AintersectBeqC intersectConst = new AintersectBeqC(idSetVar, courseIdSetForCredit,
                intersectVar);

        IntVar card = new IntVar(store, 0, MAX_COURSE_AMOUNT_PERIOD);
        CardAeqX cardConst = new CardAeqX(intersectConst.c, card);

        IntVar creditSum = new IntVar(store, 0, MAX_PERIOD_CREDIT);
        XmulCeqZ mul = new XmulCeqZ(cardConst.cardinality, credit.getCredit(),
                creditSum);

        return mul.z;
    }

    private IntVar getPeriodCreditsByCourseLevel(Store store, SetVar periodVar,
            CourseLevel level, Set<Integer> interestedCourseIdSet) {
        /*
         * Get the all courses at current period; gets the intersect with
         * eventual planned courses to select; multiply cardinality of the
         * intersect set with the credit.
         */
        Map<CourseLevel, SetDomain> creditAndDom = this.levelDomainBuilder
                .getLevelIdConstaintDomainMappingFor(interestedCourseIdSet);

        SetVar courseIdSetForLevel = new SetVar(store, creditAndDom.get(level));

        SetVar interSet = new SetVar(store, periodVar.dom());

        // intersect of current period and the course ids at the specified level
        AintersectBeqC interConst = new AintersectBeqC(periodVar, courseIdSetForLevel,
                interSet);

        IntVar creditAtThisLevel = getCredits(store, interConst.c, interestedCourseIdSet);

        return creditAtThisLevel;
    }
}
