package se.uu.it.cs.recsys.constraint.constraints;

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
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.constraint.ConstraintImposer;
import se.uu.it.cs.recsys.constraint.api.Solver;
import se.uu.it.cs.recsys.constraint.builder.DomainBuilder;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class TotalCreditsConstraint {

    private static final Logger LOGGER = LoggerFactory.getLogger(TotalCreditsConstraint.class);

    public static void impose(Store store, SetVar allIdUnion, Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds) {
        LOGGER.debug("Posting constraints on total credits!");

        Map<CourseCredit, SetVar> creditToAllIdUnionSetVar
                = ConstraintImposer.getIntersection(store,
                        allIdUnion,
                        creditToInterestedCourseIds);

        Map<CourseCredit, Integer> creditToScaledUpperLimit = ConstraintImposer
                .getCreditToScaledUpperLimit(creditToInterestedCourseIds);

        IntVar totalCredits = getTotalScaledCreditsForAllPeriods(store,
                creditToAllIdUnionSetVar,
                creditToScaledUpperLimit);

        XgteqC minCreditsConst = new XgteqC(totalCredits, Solver.MIN_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        store.impose(minCreditsConst);

        XlteqC maxCreditsConst = new XlteqC(totalCredits, Solver.MAX_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);
        store.impose(maxCreditsConst);
    }

    private static IntVar getTotalScaledCreditsForAllPeriods(Store store,
            Map<CourseCredit, SetVar> creditToAllIdUnionSetVar,
            Map<CourseCredit, Integer> creditToScaledUpperLimit) {

        List<IntVar> creditsList = creditToAllIdUnionSetVar
                .entrySet().stream()
                .map((Map.Entry<CourseCredit, SetVar> entry) -> {
                    Interval singleCreditsDomainInterval
                    = new Interval(0, creditToScaledUpperLimit.get(entry.getKey()));

                    IntDomain singleCreditsDomain = DomainBuilder
                    .createIntDomain(singleCreditsDomainInterval,
                            Solver.CREDIT_STEP_AFTER_SCALING);

                    return getScaledCredits(store,
                            entry.getKey(),
                            entry.getValue(),
                            singleCreditsDomain);
                })
                .collect(Collectors.toList());

        ArrayList<IntVar> creditsArrayList = new ArrayList<>(creditsList);

        Interval totalCreditsInterval
                = new Interval(Solver.MIN_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE,
                        Solver.MAX_ALL_PERIODS_CREDIT * Solver.CREDIT_NORMALIZATION_SCALE);

        IntVar totalCreditsVar = new IntVar(store,
                DomainBuilder.createIntDomain(totalCreditsInterval,
                        Solver.CREDIT_STEP_AFTER_SCALING));

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
    public static IntVar getScaledCredits(Store store, CourseCredit sharedCreditByAllIdFromTheSet, SetVar idSetVar, IntDomain creditsDomain) {
        IntVar card = new IntVar(store, idSetVar.dom().card());
        int scaledUnitCredit = (int) (sharedCreditByAllIdFromTheSet.getCredit() * Solver.CREDIT_NORMALIZATION_SCALE);
        IntVar credits = new IntVar(store, creditsDomain);
        store.impose(new XmulCeqZ(card, scaledUnitCredit, credits));
        return credits;
    }

}
