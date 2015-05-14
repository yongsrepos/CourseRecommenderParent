
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
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Entity
@Table(name = "supported_course_credit")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SupportedCourseCredit.findAll", query = "SELECT s FROM SupportedCourseCredit s"),
    @NamedQuery(name = "SupportedCourseCredit.findByCredit", query = "SELECT s FROM SupportedCourseCredit s WHERE s.credit = :credit")})
public class SupportedCourseCredit implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "credit")
    private Double credit;
    @OneToMany(mappedBy = "credit")
    private Collection<Course> courseCollection;

    public SupportedCourseCredit() {
    }

    public SupportedCourseCredit(Double credit) {
        this.credit = credit;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    @XmlTransient
    public Collection<Course> getCourseCollection() {
        return courseCollection;
    }

    public void setCourseCollection(Collection<Course> courseCollection) {
        this.courseCollection = courseCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (credit != null ? credit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SupportedCourseCredit)) {
            return false;
        }
        SupportedCourseCredit other = (SupportedCourseCredit) object;
        if ((this.credit == null && other.credit != null) || (this.credit != null && !this.credit.equals(other.credit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit[ credit=" + credit + " ]";
    }
    
}
