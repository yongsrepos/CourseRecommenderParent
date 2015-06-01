package se.uu.it.cs.recsys.api.type;

/*
 * #%L
 * CourseRecommenderAPI
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
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@XmlRootElement
public class CourseSelectionPreference {

    public CourseSelectionPreference() {
        // for jaxb
    }

    private Set<String> interestedCourseCodeCollection;
    private Set<String> interestedComputingDomainCollection;
    private Set<Course> mustTakeCourseCodeCollection;
    private Set<CourseSchedule> indexedScheduleInfo;
    private Set<Set<String>> avoidMoreThanOneFromTheSameCollection;

    private Double maxAdvancedCredits;
    private Double maxTotalCredits;
    private Integer maxCourseAmount;
    private Integer recommendationAmount;

    private boolean enableRuleMining;
    private Integer minFrequentItemSupport;
    private boolean enalbeDomainReasoning;

    public boolean getEnableRuleMining() {
        return enableRuleMining;
    }

    public void setEnableRuleMining(boolean enableRuleMining) {
        this.enableRuleMining = enableRuleMining;
    }

    public boolean getEnalbeDomainReasoning() {
        return enalbeDomainReasoning;
    }

    public void setEnalbeDomainReasoning(boolean enalbeDomainReasoning) {
        this.enalbeDomainReasoning = enalbeDomainReasoning;
    }

    public Integer getMinFrequentItemSupport() {
        return minFrequentItemSupport;
    }

    public void setMinFrequentItemSupport(Integer minFrequentItemSupport) {
        this.minFrequentItemSupport = minFrequentItemSupport;
    }

    public Set<Set<String>> getAvoidMoreThanOneFromTheSameCollection() {
        return avoidMoreThanOneFromTheSameCollection;
    }

    public void setAvoidMoreThanOneFromTheSameCollection(Set<Set<String>> avoidMoreThanOneFromTheSameCollection) {
        this.avoidMoreThanOneFromTheSameCollection = avoidMoreThanOneFromTheSameCollection;
    }

    public Double getMaxAdvancedCredits() {
        return maxAdvancedCredits;
    }

    public void setMaxAdvancedCredits(Double maxAdvancedCredits) {
        this.maxAdvancedCredits = maxAdvancedCredits;
    }

    public Set<String> getInterestedCourseCodeCollection() {
        return interestedCourseCodeCollection;
    }

    public void setInterestedCourseCodeCollection(Set<String> interestedCourseCodeCollection) {
        this.interestedCourseCodeCollection = interestedCourseCodeCollection;
    }

    public Set<String> getInterestedComputingDomainCollection() {
        return interestedComputingDomainCollection;
    }

    public void setInterestedComputingDomainCollection(Set<String> interestedComputingDomainCollection) {
        this.interestedComputingDomainCollection = interestedComputingDomainCollection;
    }

    public Integer getMaxCourseAmount() {
        return maxCourseAmount;
    }

    public void setMaxCourseAmount(Integer maxCourseAmount) {
        this.maxCourseAmount = maxCourseAmount;
    }

    public Double getMaxTotalCredits() {
        return maxTotalCredits;
    }

    public void setMaxTotalCredits(Double maxTotalCredits) {
        this.maxTotalCredits = maxTotalCredits;
    }

    public Integer getRecommendationAmount() {
        return recommendationAmount;
    }

    public void setRecommendationAmount(Integer recommendationAmount) {
        this.recommendationAmount = recommendationAmount;
    }

    public Set<Course> getMustTakeCourseCodeCollection() {
        return mustTakeCourseCodeCollection;
    }

    public void setMustTakeCourseCodeCollection(Set<Course> mustTakeCourseCodeCollection) {
        this.mustTakeCourseCodeCollection = mustTakeCourseCodeCollection;
    }

    public Set<CourseSchedule> getIndexedScheduleInfo() {
        return indexedScheduleInfo;
    }

    public void setIndexedScheduleInfo(Set<CourseSchedule> indexedScheduleInfo) {
        this.indexedScheduleInfo = indexedScheduleInfo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.interestedCourseCodeCollection);
        hash = 31 * hash + Objects.hashCode(this.interestedComputingDomainCollection);
        hash = 31 * hash + Objects.hashCode(this.maxCourseAmount);
        hash = 31 * hash + Objects.hashCode(this.mustTakeCourseCodeCollection);
        hash = 31 * hash + Objects.hashCode(this.maxTotalCredits);
        hash = 31 * hash + Objects.hashCode(this.recommendationAmount);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CourseSelectionPreference other = (CourseSelectionPreference) obj;
        if (!Objects.equals(this.interestedCourseCodeCollection, other.interestedCourseCodeCollection)) {
            return false;
        }
        if (!Objects.equals(this.interestedComputingDomainCollection, other.interestedComputingDomainCollection)) {
            return false;
        }
        if (!Objects.equals(this.maxCourseAmount, other.maxCourseAmount)) {
            return false;
        }
        if (!Objects.equals(this.mustTakeCourseCodeCollection, other.mustTakeCourseCodeCollection)) {
            return false;
        }
        if (!Objects.equals(this.maxTotalCredits, other.maxTotalCredits)) {
            return false;
        }
        if (!Objects.equals(this.recommendationAmount, other.recommendationAmount)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CourseSelectionPreference{" + "interestedCourseCodeCollection=" + interestedCourseCodeCollection + ", interestedComputingDomainCollection=" + interestedComputingDomainCollection + ", maxCourseAmount=" + maxCourseAmount + ", mustTakeCourseCodeCollection=" + mustTakeCourseCodeCollection + ", maxTotalCredits=" + maxTotalCredits + ", recommendationAmount=" + recommendationAmount + '}';
    }

}
