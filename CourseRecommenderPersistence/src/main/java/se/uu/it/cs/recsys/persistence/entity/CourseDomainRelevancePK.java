
package se.uu.it.cs.recsys.persistence.entity;

/*
 * #%L
 * CourseRecommenderPersistence
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


import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Embeddable
public class CourseDomainRelevancePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "course_id")
    private int courseId;
    @Basic(optional = false)
    @Column(name = "domain_id")
    private String domainId;

    public CourseDomainRelevancePK() {
    }

    public CourseDomainRelevancePK(int courseId, String domainId) {
        this.courseId = courseId;
        this.domainId = domainId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) courseId;
        hash += (domainId != null ? domainId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseDomainRelevancePK)) {
            return false;
        }
        CourseDomainRelevancePK other = (CourseDomainRelevancePK) object;
        if (this.courseId != other.courseId) {
            return false;
        }
        if ((this.domainId == null && other.domainId != null) || (this.domainId != null && !this.domainId.equals(other.domainId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevancePK[ courseId=" + courseId + ", domainId=" + domainId + " ]";
    }
    
}
