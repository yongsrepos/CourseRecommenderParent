package se.uu.it.cs.recsys.constraint.api;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.api.type.CourseSchedule;
import se.uu.it.cs.recsys.constraint.ConstraintImposer;
import se.uu.it.cs.recsys.constraint.builder.ScheduleDomainBuilder;
import se.uu.it.cs.recsys.constraint.constraints.AllPeriodsCourseIdUnionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.AvoidSameCourseConstraint;
import se.uu.it.cs.recsys.constraint.constraints.CourseCardinalityConstraint;
import se.uu.it.cs.recsys.constraint.constraints.FixedCourseSelectionConstraint;
import se.uu.it.cs.recsys.constraint.constraints.TotalCreditsConstraint;
import se.uu.it.cs.recsys.constraint.util.ConstraintResultConverter;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.util.CourseFlattener;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Service
public class Solver {
    /*
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);

    private final Set<Integer> interestedCourseIdSet = new HashSet<>();

    private final Set<Course> interestedCourses = new HashSet<>();

    private final Map<CourseLevel, Set<Integer>> levelToInterestedCourseIds
            = new HashMap<>();

    private final Map<CourseCredit, Set<Integer>> creditToInterestedCourseIds
            = new HashMap<>();

    private final Map<Integer, Set<Integer>> periodNumToMustHaveIdSet
            = new HashMap<>();

    private final List<Set<Integer>> sameCourseWithDiffIdInPlanYear
            = new ArrayList<>();

    private final Set<CourseSchedule> schedulePeriodsInfo = new HashSet<>();

    public static final int DEFAULT_SUGGESTION_AMOUNT = 1;

    private int suggestionAmount = DEFAULT_SUGGESTION_AMOUNT;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ScheduleDomainBuilder scheduleDomainBuilder;

    @Autowired
    private ConstraintResultConverter resultConverter;

    public static final int TOTAL_PLAN_PERIODS = 6;

    public static final int MIN_PERIOD_CREDIT = 10;
    public static final int MAX_PERIOD_CREDIT = 30;
    public static final int MIN_SEMESTER_CREDIT = 28;
    public static final int MAX_SEMESTER_CREDIT = 35;
    public static final int MIN_ADVANCED_CREDIT = 60;
    public static final int MIN_ALL_PERIODS_CREDIT = 90;
    public static final int MAX_ALL_PERIODS_CREDIT = MAX_PERIOD_CREDIT * TOTAL_PLAN_PERIODS;

    /**
     * This value is use to multiply the original credit value, so that it can
     * turn the float value into a integer one, for that to use constraint on
     * integers.
     */
    public static final int CREDIT_NORMALIZATION_SCALE = 10;

    public static final int CREDIT_STEP_AFTER_SCALING = (int) (0.5 * CREDIT_NORMALIZATION_SCALE);

    public static final int MIN_COURSE_AMOUNT_PERIOD = 1;
    public static final int MAX_COURSE_AMOUNT_PERIOD = 4;

    /**
     *
     * @return a random course selection suggestion that matches a degree
     * requirements
     */
    public List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> getRandomSolution() {

        return getSolutionWithPreference(this.courseRepository.findAllDistinctAutoGenId());

    }

    /**
     *
     * @param argInterestedCourseIdSet, non-empty input
     * @return a course selection suggestion that matches a degree requirements,
     * with candidates from the input; or an empty array if no satisfactory
     * solution found
     * @throws IllegalArgumentException if input is null or empty
     */
    public List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> getSolutionWithPreference(Set<Integer> argInterestedCourseIdSet) {

        if (argInterestedCourseIdSet == null || argInterestedCourseIdSet.isEmpty()) {
            throw new IllegalArgumentException("Input must be non-empty!");
        }

        initFields(argInterestedCourseIdSet);

        Store store = new Store();

        SetVar[] pers = initSetVars(store, this.interestedCourseIdSet);

        postConstraints(store, pers);

        return search(store, pers);
    }

    private void initFields(Set<Integer> argInterestedCourseIdSet) {
        //1. init interested id set
        this.interestedCourseIdSet.addAll(argInterestedCourseIdSet);

        //2. init interest course set
        this.interestedCourses.addAll(this.courseRepository.findByAutoGenIds(this.interestedCourseIdSet));

        //3. categorize course set based on credit
        CourseFlattener.flattenToCreditAndIdSetMap(this.interestedCourses)
                .entrySet().stream()
                .forEach(entry -> {
                    this.creditToInterestedCourseIds.put(
                            CourseCredit.ofValue(entry.getKey().getCredit().floatValue()),
                            entry.getValue());
                });

        //4. categorize course set based on level
        CourseFlattener.flattenToLevelAndIdSetMap(this.interestedCourses)
                .entrySet().stream()
                .forEach(entry -> {
                    this.levelToInterestedCourseIds.put(
                            CourseLevel.ofDBString(entry.getKey().getLevel()),
                            entry.getValue());
                });

        //5. num the periods
        setDefaultPeriodsScheduleInfo();

        //6. set same course with diff codes
        initSameCourseWithDiffIdInPlanYear();
    }

    // 1. Init each period as SetVar
    private SetVar[] initSetVars(Store store, Set<Integer> interestedCourseIdSet) {
        LOGGER.debug("Initiate var for each study period.");

        SetVar[] p = new SetVar[6];

        Map<Integer, SetDomain> domains = this.scheduleDomainBuilder
                .createScheduleSetDomainsFor(interestedCourseIdSet, this.schedulePeriodsInfo);

        for (int i = 0; i <= 5; i++) {
            p[i] = new SetVar(store, "Period_" + (i + 1), domains.get(i + 1));
        }

        return p;
    }

    private void postConstraints(Store store, SetVar[] pers) {
        CourseCardinalityConstraint.impose(store, pers);

        FixedCourseSelectionConstraint.impose(store, pers, periodNumToMustHaveIdSet);
        SetVar allCourseIdUnion = AllPeriodsCourseIdUnionConstraint.imposeAndGetUnion(store, pers, this.interestedCourseIdSet);

        ConstraintImposer.postAdvancedCreditsConst(store, allCourseIdUnion, getCreditToInterestedAdvancedCourseIdSetMapping());
        TotalCreditsConstraint.impose(store, allCourseIdUnion, this.creditToInterestedCourseIds);
        ConstraintImposer.postEachPeriodCreditConst(store, pers, this.creditToInterestedCourseIds);

        AvoidSameCourseConstraint.impose(store, allCourseIdUnion, this.sameCourseWithDiffIdInPlanYear);

    }

    private Map<CourseCredit, Set<Integer>> getCreditToInterestedAdvancedCourseIdSetMapping() {
        Set<Course> interestedAdvancedCourseSet = this.interestedCourses.stream()
                .filter(course -> course.getLevel().getLevel()
                        .equals(CourseLevel.ADVANCED.getDBString()))
                .collect(Collectors.toSet());

        return CourseFlattener.flattenToCreditAndIdSetMap(interestedAdvancedCourseSet)
                .entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    return CourseCredit.ofValue(entry.getKey().getCredit().floatValue());
                }, Map.Entry::getValue));
    }

    private List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> search(Store store, SetVar[] vars) {
        LOGGER.debug("Start searching solution ... ");

        Domain[][] solutions = doSearch(store, vars);

        if (solutions == null || solutions.length == 0) {
            LOGGER.debug("No solution found!");
            return Collections.EMPTY_LIST;
        }

        List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> result = new ArrayList<>();

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
        LOGGER.debug("Store constraints num: {}, consistency: {}", store.numberConstraints(), store.consistency());

        Search<SetVar> search = new DepthFirstSearch<>();

        SelectChoicePoint<SetVar> select = new SimpleSelect<>(
                vars,
                new MaxCardDiff<>(),
                new IndomainSetMax<>()
        );
        search.setSolutionListener(new SimpleSolutionListener<>());
        search.getSolutionListener().setSolutionLimit(this.suggestionAmount);

        search.getSolutionListener().recordSolutions(true);

        search.labeling(store, select);

        LOGGER.debug("Wrong decisions: {}", search.getWrongDecisions());

        return search.getSolutionListener().getSolutions();
    }

    /**
     * A suggestion covers course selection for all study periods.
     *
     * @param suggestionAmount must be positive, specified how many of such
     * suggestions will be generated.If not specified, a default value of
     * {@link #DEFAULT_SUGGESTION_AMOUNT} will be applied.
     *
     * @throws IllegalArgumentException if input is less than one.
     */
    public void setSuggestionAmount(int suggestionAmount) {
        if (suggestionAmount < 1) {
            throw new IllegalArgumentException("Input must be bigger than 0!");
        }

        this.suggestionAmount = suggestionAmount;
    }

    public void addMandatoryCourse(Set<se.uu.it.cs.recsys.api.type.Course> mandatoryCourseSet) {
        if (mandatoryCourseSet == null || mandatoryCourseSet.isEmpty()) {
            LOGGER.info("No mandatory selection set.");
            return;
        }
        mandatoryCourseSet.forEach(course -> {
            Course courseEntity = this.courseRepository.findByCodeAndTaughtYearAndStartPeriod(course.getCode(),
                    course.getTaughtYear().shortValue(),
                    course.getStartPeriod().shortValue());

            int periodIdx = findSchedulePeriodIndex(course.getTaughtYear().shortValue(),
                    course.getStartPeriod().shortValue());

            if (this.periodNumToMustHaveIdSet.containsKey(periodIdx)) {
                this.periodNumToMustHaveIdSet.get(periodIdx).add(courseEntity.getAutoGenId());
            } else {
                this.periodNumToMustHaveIdSet.put(periodIdx,
                        Stream.of(courseEntity.getAutoGenId()).collect(Collectors.toSet()));
            }
        });

    }

    private int findSchedulePeriodIndex(short taughtYear, short startPeriod) {
        for (CourseSchedule schedule : this.schedulePeriodsInfo) {
            if (schedule.getTaughtYear() == taughtYear && schedule.getStartPeriod() == startPeriod) {
                return schedule.getPeriodIdxAmongAllPlanPeriods();
            }
        }

        throw new IllegalStateException("Has not schedule info for taught year " + taughtYear + ", start period " + startPeriod);
    }

    public void addMandatoryPlan(Map<Integer, Set<Integer>> periodOneBasedIdxToMustHaveIdSet) {
        if (periodOneBasedIdxToMustHaveIdSet == null || periodOneBasedIdxToMustHaveIdSet.isEmpty()) {
            LOGGER.info("No mandatory selection set.");
            return;
        }

        this.periodNumToMustHaveIdSet.putAll(periodOneBasedIdxToMustHaveIdSet);
    }

    private void setDefaultPeriodsScheduleInfo() {
        if (this.schedulePeriodsInfo.isEmpty()) {
            this.schedulePeriodsInfo.addAll(
                    Stream.of(new CourseSchedule((short) 2015, (short) 1, (short) 1),
                            new CourseSchedule((short) 2015, (short) 2, (short) 2),
                            new CourseSchedule((short) 2016, (short) 3, (short) 3),
                            new CourseSchedule((short) 2016, (short) 4, (short) 4),
                            new CourseSchedule((short) 2016, (short) 1, (short) 5),
                            new CourseSchedule((short) 2016, (short) 2, (short) 6))
                    .collect(Collectors.toSet())
            );

        }
    }

    private List<Set<Integer>> getSameCourseWithDiffCodeInPlanYear() {
        // for example, course 1DL301 and 1DL300, both are Database Design I
        Set<Integer> dl300IdSet = this.courseRepository
                .findByCode("1DL300")
                .stream()
                .filter(course -> course.getTaughtYear() >= 2015)
                .flatMap(course -> Stream.of(course.getAutoGenId()))
                .collect(Collectors.toSet());

        Set<Integer> dl301IdSet = this.courseRepository
                .findByCode("1DL301")
                .stream()
                .filter(course -> course.getTaughtYear() >= 2015)
                .flatMap(course -> Stream.of(course.getAutoGenId()))
                .collect(Collectors.toSet());

        dl300IdSet.addAll(dl301IdSet);

        return Stream.of(dl300IdSet).collect(Collectors.toList());
    }

    private void initSameCourseWithDiffIdInPlanYear() {
        this.sameCourseWithDiffIdInPlanYear.addAll(getSameCourseWithDiffCodeInPlanYear());

        List<Course> allCourses = this.courseRepository.findAll();
        Set<Course> planYearCourseSet = allCourses.stream()
                .filter(course -> course.getTaughtYear() >= 2015)
                .collect(Collectors.toSet());

        Map<String, Set<Integer>> codeToIdSet = CourseFlattener.flattenToCodeAndIdSet(planYearCourseSet);

        codeToIdSet.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .forEach(entry
                        -> {
                    LOGGER.debug("code: {}, idSet {}", entry.getKey(), entry.getValue());
                    this.sameCourseWithDiffIdInPlanYear.add(entry.getValue());
                });
    }
}
