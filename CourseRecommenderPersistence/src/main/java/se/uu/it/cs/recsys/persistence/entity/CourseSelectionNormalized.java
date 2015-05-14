
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
@Table(name = "course_selection_normalized")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseSelectionNormalized.findAll", query = "SELECT c FROM CourseSelectionNormalized c"),
    @NamedQuery(name = "CourseSelectionNormalized.findMaxCourseSelectionNormalizedPKStudentId", query = "SELECT MAX(c.courseSelectionNormalizedPK.studentId) FROM CourseSelectionNormalized c"),
    @NamedQuery(name = "CourseSelectionNormalized.findCourseSelectionNormalizedPKNormalizedCourseIdByCourseSelectionNormalizedPKStudentId", query = "SELECT c.courseSelectionNormalizedPK.normalizedCourseId FROM CourseSelectionNormalized c WHERE c.courseSelectionNormalizedPK.studentId = :studentId"),
    @NamedQuery(name = "CourseSelectionNormalized.findByCourseSelectionNormalizedPKStudentId", query = "SELECT c FROM CourseSelectionNormalized c WHERE c.courseSelectionNormalizedPK.studentId = :studentId"),
    @NamedQuery(name = "CourseSelectionNormalized.findByCourseSelectionNormalizedPKNormalizedCourseId", query = "SELECT c FROM CourseSelectionNormalized c WHERE c.courseSelectionNormalizedPK.normalizedCourseId = :normalizedCourseId")})
public class CourseSelectionNormalized implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CourseSelectionNormalizedPK courseSelectionNormalizedPK;
    @JoinColumn(name = "normalized_course_id", referencedColumnName = "auto_gen_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Course course;

    public CourseSelectionNormalized() {
    }

    public CourseSelectionNormalized(CourseSelectionNormalizedPK courseSelectionNormalizedPK) {
        this.courseSelectionNormalizedPK = courseSelectionNormalizedPK;
    }

    public CourseSelectionNormalized(int studentId, int normalizedCourseId) {
        this.courseSelectionNormalizedPK = new CourseSelectionNormalizedPK(studentId, normalizedCourseId);
    }

    public CourseSelectionNormalizedPK getCourseSelectionNormalizedPK() {
        return courseSelectionNormalizedPK;
    }

    public void setCourseSelectionNormalizedPK(CourseSelectionNormalizedPK courseSelectionNormalizedPK) {
        this.courseSelectionNormalizedPK = courseSelectionNormalizedPK;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseSelectionNormalizedPK != null ? courseSelectionNormalizedPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseSelectionNormalized)) {
            return false;
        }
        CourseSelectionNormalized other = (CourseSelectionNormalized) object;
        if ((this.courseSelectionNormalizedPK == null && other.courseSelectionNormalizedPK != null) || (this.courseSelectionNormalizedPK != null && !this.courseSelectionNormalizedPK.equals(other.courseSelectionNormalizedPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseSelectionNormalized[ courseSelectionNormalizedPK=" + courseSelectionNormalizedPK + " ]";
    }
    
}
