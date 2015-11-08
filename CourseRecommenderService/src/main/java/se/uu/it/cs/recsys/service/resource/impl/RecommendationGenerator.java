package se.uu.it.cs.recsys.service.resource.impl;

/*
 * #%L
 * CourseRecommenderService
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.api.type.CourseSelectionPreference;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;
import se.uu.it.cs.recsys.constraint.api.SolverAPI;
import se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevance;
import se.uu.it.cs.recsys.persistence.repository.CourseDomainRelevanceRepository;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.ruleminer.api.RuleMiner;
import se.uu.it.cs.recsys.semantic.ComputingDomainReasoner;
import se.uu.it.cs.recsys.service.preference.ConstraintSolverPreferenceBuilder;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class RecommendationGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationGenerator.class);

    public static final Integer MIN_FREQUENT_RULE_SUPPORT = 15;

    @Autowired
    private ConstraintSolverPreferenceBuilder prefBuilder;

    @Autowired
    private ComputingDomainReasoner computingDomainReasoner;

    @Autowired
    private CourseDomainRelevanceRepository courseDomainRelevanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SolverAPI constraintSolver;

    @Autowired
    private RuleMiner ruleMiner;

    private CourseSelectionPreference userPref;

    private ConstraintSolverPreference constraintPref;

    public Set<List<Course>> generateRcommendation(CourseSelectionPreference userPref) {

        this.userPref = userPref;

        this.constraintPref = this.prefBuilder
                .build(userPref);

        Set<List<Course>> recommendations = this.constraintSolver
                .getSolution(this.constraintPref);

        if (recommendations.isEmpty()) {

            recommendations = retry();
        }

        return recommendations;
    }

    private Set<List<Course>> retry() {

        LOGGER.info("No solution found yet. Retry with inferred interested courses!");

        Set<List<Course>> solution = new HashSet<>();

        if (this.userPref.getEnalbeDomainReasoning()) {

            LOGGER.info("Retry with domain reasoning!");

            Set<Integer> fromDomainReasoning
                    = getInterestCourseIdFromDomainIds();

            if (!fromDomainReasoning.isEmpty()) {
                solution = doRetry(new ArrayList(fromDomainReasoning));
            }
        }

        if (solution.isEmpty()) {
            LOGGER.info("No solution found yet with domain reasoning!");
        }

        if (this.userPref.getEnableRuleMining()) {

            LOGGER.info("Retry with frequent pattern mining!");

            Set<Integer> userSpecifiedInteresedCourseId = this.constraintPref
                    .getInterestedCourseIdCollection();

            Integer minFrequentItemSupport
                    = this.userPref.getMinFrequentItemSupport() == null
                            ? MIN_FREQUENT_RULE_SUPPORT
                            : userPref.getMinFrequentItemSupport();

            Set<Integer> interestedCourseIdFromRuleMining
                    = getInterestedCourseIdFromRuleMining(
                            userSpecifiedInteresedCourseId,
                            minFrequentItemSupport);

            if (!interestedCourseIdFromRuleMining.isEmpty()) {
                solution = doRetry(new ArrayList(interestedCourseIdFromRuleMining));
            }
        }
        return solution;

    }

    private Set<List<Course>> doRetry(List<Integer> inferredIdSet) {

        Set<List<Course>> solution = new HashSet<>();

        final int step = 10;
        int i = step;

        while (i < inferredIdSet.size()) {
            LOGGER.info("Retrying time {}", i / step);

            List<Integer> retryCandidates = inferredIdSet.subList(i - step, i);

            this.constraintPref.getInterestedCourseIdCollection()
                    .addAll(retryCandidates);

            solution = this.constraintSolver.getSolution(this.constraintPref);

            if (!solution.isEmpty()) {
                return solution;
            }

            i = i + step;
        }

        if (inferredIdSet.size() % step != 0) {
            LOGGER.info("Retrying time {}", i / step );

            List<Integer> retryCandidates;

            if (i > inferredIdSet.size()) {
                retryCandidates = inferredIdSet.subList(i - step, inferredIdSet.size());
            } else {
                retryCandidates = inferredIdSet;
            }

            this.constraintPref.getInterestedCourseIdCollection()
                    .addAll(retryCandidates);

            solution = this.constraintSolver.getSolution(this.constraintPref);
        }

        return solution;
    }

    private Set<Integer> getInterestCourseIdFromDomainIds() {

        Set<String> interestedDomainIdSet = this.userPref.getInterestedComputingDomainCollection();

        if (interestedDomainIdSet == null || interestedDomainIdSet.isEmpty()) {
            return Collections.emptySet();
        }

        LOGGER.debug("Interested domain id set {}", interestedDomainIdSet);

        Set<String> totalInterestedDomaindIdSet = new HashSet<>(interestedDomainIdSet);

        interestedDomainIdSet.forEach(domainId -> {
            try {
                Set<String> subDomains = this.computingDomainReasoner.getNarrowerDomainIds(domainId);

                if (!subDomains.isEmpty()) {
                    totalInterestedDomaindIdSet.addAll(subDomains);
                }

                Set<String> relatedDomains = this.computingDomainReasoner.getRelatedDomainIds(domainId);

                if (!relatedDomains.isEmpty()) {
                    totalInterestedDomaindIdSet.addAll(relatedDomains);
                }

            } catch (IOException ex) {
                LOGGER.error("Failed on domain reasoning for domain id {}", domainId, ex);
            }
        });

        Set<Integer> interestedCourseIdCollection = new HashSet<>();

        totalInterestedDomaindIdSet.forEach(domainId -> {
            Set<CourseDomainRelevance> relevance = this.courseDomainRelevanceRepository
                    .findByDomainId(domainId);

            relevance.forEach(entityRel -> {
                Integer courseId = entityRel.getCourseDomainRelevancePK().getCourseId();
                interestedCourseIdCollection.add(courseId);
            });
        });

        LOGGER.debug("Interested course id generated from computing domain id set {}",
                interestedCourseIdCollection);

        return interestedCourseIdCollection;
    }

    Set<Integer> getInterestedCourseIdFromRuleMining(Set<Integer> userSpecifiedCourseIdCollection, Integer minSupport) {

        Set<Integer> interestedCourseIdSet = new HashSet();

        Map<Set<Integer>, Integer> rules = this.ruleMiner
                .getPatterns(userSpecifiedCourseIdCollection, minSupport);

        rules.entrySet().stream()
                .forEach(entry -> {
                    if (!entry.getKey().isEmpty()) {
                        interestedCourseIdSet.addAll(entry.getKey());
                    }
                });

        return filterOnPlanYear(interestedCourseIdSet);
    }

    Set<Integer> filterOnPlanYear(Set<Integer> courseId) {
        if (courseId.isEmpty()) {
            return Collections.emptySet();
        }

        Set<se.uu.it.cs.recsys.persistence.entity.Course> filteredCourse = this.courseRepository.findByAutoGenIds(courseId)
                .stream()
                .filter(ConstraintSolverPreferenceBuilder
                        .inPlanYearPredicate(this.constraintPref.getIndexedScheduleInfo()))
                .sorted((se.uu.it.cs.recsys.persistence.entity.Course one,
                                se.uu.it.cs.recsys.persistence.entity.Course two)
                        -> Integer.compare(one.getAutoGenId(), two.getAutoGenId()))
                .collect(Collectors.toSet());

        filteredCourse.forEach(course -> LOGGER.debug("Filtered course: {}", course));

        return filteredCourse
                .stream()
                .map(course -> course.getAutoGenId())
                .collect(Collectors.toSet());
    }

}
