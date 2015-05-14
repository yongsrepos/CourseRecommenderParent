
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
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Entity
@Table(name = "course_domain_relevance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseDomainRelevance.findAll", query = "SELECT c FROM CourseDomainRelevance c"),
    @NamedQuery(name = "CourseDomainRelevance.findByCourseId", query = "SELECT c FROM CourseDomainRelevance c WHERE c.courseDomainRelevancePK.courseId = :courseId"),
    @NamedQuery(name = "CourseDomainRelevance.findByDomainId", query = "SELECT c FROM CourseDomainRelevance c WHERE c.courseDomainRelevancePK.domainId = :domainId"),
    @NamedQuery(name = "CourseDomainRelevance.findByRelevanceLevel", query = "SELECT c FROM CourseDomainRelevance c WHERE c.relevanceLevel = :relevanceLevel")})
public class CourseDomainRelevance implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CourseDomainRelevancePK courseDomainRelevancePK;
    @Column(name = "relevance_level")
    private Short relevanceLevel;
    @JoinColumn(name = "course_id", referencedColumnName = "auto_gen_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Course course;
    @JoinColumn(name = "domain_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private ComputingDomain computingDomain;

    public CourseDomainRelevance() {
    }

    public CourseDomainRelevance(CourseDomainRelevancePK courseDomainRelevancePK) {
        this.courseDomainRelevancePK = courseDomainRelevancePK;
    }

    public CourseDomainRelevance(int courseId, String domainId) {
        this.courseDomainRelevancePK = new CourseDomainRelevancePK(courseId, domainId);
    }

    public CourseDomainRelevancePK getCourseDomainRelevancePK() {
        return courseDomainRelevancePK;
    }

    public void setCourseDomainRelevancePK(CourseDomainRelevancePK courseDomainRelevancePK) {
        this.courseDomainRelevancePK = courseDomainRelevancePK;
    }

    public Short getRelevanceLevel() {
        return relevanceLevel;
    }

    public void setRelevanceLevel(Short relevanceLevel) {
        this.relevanceLevel = relevanceLevel;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ComputingDomain getComputingDomain() {
        return computingDomain;
    }

    public void setComputingDomain(ComputingDomain computingDomain) {
        this.computingDomain = computingDomain;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseDomainRelevancePK != null ? courseDomainRelevancePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseDomainRelevance)) {
            return false;
        }
        CourseDomainRelevance other = (CourseDomainRelevance) object;
        if ((this.courseDomainRelevancePK == null && other.courseDomainRelevancePK != null) || (this.courseDomainRelevancePK != null && !this.courseDomainRelevancePK.equals(other.courseDomainRelevancePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevance[ courseDomainRelevancePK=" + courseDomainRelevancePK + " ]";
    }
    
}
