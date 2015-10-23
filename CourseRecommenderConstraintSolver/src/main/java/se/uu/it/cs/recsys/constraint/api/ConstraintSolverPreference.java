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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import se.uu.it.cs.recsys.api.type.CourseSchedule;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class ConstraintSolverPreference {

    public static final Double MAX_TOTAL_CREDIT_DEFAULT = 120.0;
    public static final Double MIN_TOTAL_CREDIT = 90.0;

    public static final Double MIN_ADVANCED_CREDIT = 60.0;
    public static final Double MAX_ADVANCED_CREDIT_DEFAULT = ConstraintSolverPreference.MAX_TOTAL_CREDIT_DEFAULT;

    public static final int RECOMMENDATION_AMOUNT_DEFAULT = 3;
    
    public static final int MIN_COURSE_AMOUNT_EACH_PERIOD = 1;
    public static final int MAX_COURSE_AMOUNT_EACH_PERIOD = 3;

    public static final Double MIN_PERIOD_CREDIT = 10.0;
    public static final Double MAX_PERIOD_CREDIT = 30.0;

    public static final int MAX_TOTAL_COURSE_AMOUNT_DEFAULT = 15;
    public static final int MIN_TOTAL_COURSE_AMOUNT_DEFAULT = 6;

    private Set<Integer> interestedCourseIdCollection;

    private Integer maxCourseAmount;

    private Map<Integer, Set<Integer>> periodIdxToMustTakeCourseId;

    private Set<Set<Integer>> avoidCollectionForCourseWithDiffIdSet;

    private Double maxTotalCredits;

    private Double maxAdvancedCredits;

    private Integer recommendationAmount;

    private Set<CourseSchedule> indexedScheduleInfo;

    private ConstraintSolverPreference() {
    }

    public static class Builder {

        private Integer maxCourseAmount;

        private Double maxTotalCredits;

        private Double maxAdvancedCredits;

        private Integer recommendationAmount;

        private Map<Integer, Set<Integer>> periodIdxToMustTakeCourseId;

        private Set<Integer> interestedCourseIdCollection;

        private Set<Set<Integer>> avoidCollectionForCourseWithDiffIdSet;

        private Set<CourseSchedule> indexedScheduleInfo;

        /**
         * Implements the Builder pattern. Schedule info and interested course
         * id collection can not be empty.
         *
         * @return non-null instance, with non-null fields. E.g if Max total
         * credits is not set by input, then it will use the default value;
         *
         * @throws IllegalStateException if schedule info or interested course
         * id collection is null or empty.
         */
        public ConstraintSolverPreference build() {
            ConstraintSolverPreference instance = new ConstraintSolverPreference();

            if (this.indexedScheduleInfo == null || this.indexedScheduleInfo.isEmpty()) {
                throw new IllegalStateException("Schedule info can not be null!");
            }

            if (this.interestedCourseIdCollection == null || this.interestedCourseIdCollection.isEmpty()) {
                throw new IllegalStateException("Interested course id set can not be null or empty!");
            }

            if (this.maxCourseAmount == null) {
                this.maxCourseAmount = MAX_TOTAL_COURSE_AMOUNT_DEFAULT;
            }

            if (this.maxTotalCredits == null) {
                this.maxTotalCredits = MAX_TOTAL_CREDIT_DEFAULT;
            }

            if (this.maxAdvancedCredits == null) {
                this.maxAdvancedCredits = MAX_ADVANCED_CREDIT_DEFAULT;
            }

            if (this.recommendationAmount == null) {
                this.recommendationAmount = RECOMMENDATION_AMOUNT_DEFAULT;
            }

            if (this.periodIdxToMustTakeCourseId == null) {
                this.periodIdxToMustTakeCourseId = new HashMap<>();
            }

            if (this.avoidCollectionForCourseWithDiffIdSet == null) {
                this.avoidCollectionForCourseWithDiffIdSet = new HashSet<>();
            }

            instance.setMaxAdvancedCredits(this.maxAdvancedCredits);
            instance.setMaxCourseAmount(this.maxCourseAmount);
            instance.setMaxTotalCredits(this.maxTotalCredits);
            instance.setRecommendationAmount(this.recommendationAmount);
            instance.avoidCollectionForCourseWithDiffIdSet = this.avoidCollectionForCourseWithDiffIdSet;
            instance.indexedScheduleInfo = this.indexedScheduleInfo;
            instance.interestedCourseIdCollection = this.interestedCourseIdCollection;
            instance.periodIdxToMustTakeCourseId = this.periodIdxToMustTakeCourseId;

            return instance;
        }

        public Builder setInterestedCourseIdCollection(Set<Integer> interestedCourseIdCollection) {
            this.interestedCourseIdCollection = interestedCourseIdCollection;
            return this;
        }

        public Builder setMaxCourseAmount(Integer maxCourseAmount) {
            this.maxCourseAmount = maxCourseAmount;
            return this;
        }

        public Builder setMaxTotalCredits(Double maxAdvancedCredits) {
            this.maxAdvancedCredits = maxAdvancedCredits;
            return this;
        }

        public Builder setMaxAdvancedCredits(Double maxAdvancedCredits) {
            this.maxAdvancedCredits = maxAdvancedCredits;
            return this;
        }

        public Builder setRecommendationAmount(Integer recommendationAmount) {
            this.recommendationAmount = recommendationAmount;
            return this;
        }

        public Builder setIndexedScheduleInfo(Set<CourseSchedule> indexedScheduleInfo) {
            this.indexedScheduleInfo = indexedScheduleInfo;
            return this;
        }

        public Builder setPeriodIdxToMustTakeCourseId(
                Map<Integer, Set<Integer>> periodIdxToMustTakeCourseId) {

            this.periodIdxToMustTakeCourseId = periodIdxToMustTakeCourseId;
            return this;

        }

        public Builder setAvoidCollectionForCourseWithDiffIdSet(
                Set<Set<Integer>> avoidCollectionForCourseWithDiffIdSet) {

            this.avoidCollectionForCourseWithDiffIdSet = avoidCollectionForCourseWithDiffIdSet;
            return this;
        }
    }

    public Double getMaxAdvancedCredits() {
        return maxAdvancedCredits;
    }

    public void setMaxAdvancedCredits(Double maxAdvancedCredits) {
        if (maxAdvancedCredits == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }

        this.maxAdvancedCredits = maxAdvancedCredits;
    }

    public Set<CourseSchedule> getIndexedScheduleInfo() {
        return indexedScheduleInfo;
    }

    public Set<Integer> getInterestedCourseIdCollection() {
        return interestedCourseIdCollection;
    }

    public Integer getMaxCourseAmount() {
        return maxCourseAmount;
    }

    public void setMaxCourseAmount(Integer maxCourseAmount) {

        if (maxCourseAmount == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }

        this.maxCourseAmount = maxCourseAmount;
    }

    public Map<Integer, Set<Integer>> getPeriodIdxToMustTakeCourseId() {
        return periodIdxToMustTakeCourseId;
    }

    public Set<Set<Integer>> getAvoidCollectionForCourseWithDiffIdSet() {
        return avoidCollectionForCourseWithDiffIdSet;
    }

    public Double getMaxTotalCredits() {
        return maxTotalCredits;
    }

    public void setMaxTotalCredits(Double maxTotalCredits) {

        if (maxTotalCredits == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }

        this.maxTotalCredits = maxTotalCredits;
    }

    public Integer getRecommendationAmount() {
        return recommendationAmount;
    }

    public void setRecommendationAmount(Integer recommendationAmount) {
        if (recommendationAmount == null) {
            throw new IllegalArgumentException("Input can not be null!");
        }

        this.recommendationAmount = recommendationAmount;
    }

}
