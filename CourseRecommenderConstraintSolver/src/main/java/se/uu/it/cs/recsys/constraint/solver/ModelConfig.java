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


import java.util.Map;
import java.util.Set;
import se.uu.it.cs.recsys.api.type.CourseCredit;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class ModelConfig {

    private Double maxTotalCredits;

    private Double maxAdvancedCredits;
    
    private Integer maxCourseAmount;
    
    private Set<Integer> interestedCourseIdCollection;
    
    private Set<Set<Integer>> avoidCollectionForCourseWithDiffIdSet;
    
    private Map<Integer, Set<Integer>> periodIdxToMustHaveCourseId;

    private Map<CourseCredit, Set<Integer>> creditToCourseId;

    private Map<CourseCredit, Set<Integer>> creditToAdvancedId;

    public Integer getMaxCourseAmount() {
        return maxCourseAmount;
    }

    public void setMaxCourseAmount(Integer maxCourseAmount) {
        this.maxCourseAmount = maxCourseAmount;
    }

    public Set<Set<Integer>> getAvoidCollectionForCourseWithDiffIdSet() {
        return avoidCollectionForCourseWithDiffIdSet;
    }

    public void setAvoidCollectionForCourseWithDiffIdSet(Set<Set<Integer>> avoidCollectionForCourseWithDiffIdSet) {
        this.avoidCollectionForCourseWithDiffIdSet = avoidCollectionForCourseWithDiffIdSet;
    }

    public Set<Integer> getInterestedCourseIdCollection() {
        return interestedCourseIdCollection;
    }

    public void setInterestedCourseIdCollection(Set<Integer> interestedCourseIdCollection) {
        this.interestedCourseIdCollection = interestedCourseIdCollection;
    }

    public Double getMaxTotalCredits() {
        return maxTotalCredits;
    }

    public void setMaxTotalCredits(Double maxTotalCredits) {
        this.maxTotalCredits = maxTotalCredits;
    }

    public Double getMaxAdvancedCredits() {
        return maxAdvancedCredits;
    }

    public void setMaxAdvancedCredits(Double maxAdvancedCredits) {
        this.maxAdvancedCredits = maxAdvancedCredits;
    }
    
    public Map<Integer, Set<Integer>> getPeriodIdxToMustHaveCourseId() {
        return periodIdxToMustHaveCourseId;
    }

    public void setPeriodIdxToMustHaveCourseId(Map<Integer, Set<Integer>> periodIdxToMustHaveCourseId) {
        this.periodIdxToMustHaveCourseId = periodIdxToMustHaveCourseId;
    }

    public Map<CourseCredit, Set<Integer>> getCreditToCourseId() {
        return creditToCourseId;
    }

    public void setCreditToCourseId(Map<CourseCredit, Set<Integer>> creditToCourseId) {
        this.creditToCourseId = creditToCourseId;
    }

    public Map<CourseCredit, Set<Integer>> getCreditToAdvancedId() {
        return creditToAdvancedId;
    }

    public void setCreditToAdvancedId(Map<CourseCredit, Set<Integer>> creditToAdvancedId) {
        this.creditToAdvancedId = creditToAdvancedId;
    }

}
