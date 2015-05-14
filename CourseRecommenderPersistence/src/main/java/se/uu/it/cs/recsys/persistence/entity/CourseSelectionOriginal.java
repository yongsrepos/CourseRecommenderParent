
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
@Table(name = "course_selection_original")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseSelectionOriginal.findAll", query = "SELECT c FROM CourseSelectionOriginal c"),
    @NamedQuery(name = "CourseSelectionOriginal.findByStudentId", query = "SELECT c FROM CourseSelectionOriginal c WHERE c.courseSelectionOriginalPK.studentId = :studentId"),
    @NamedQuery(name = "CourseSelectionOriginal.findByCourseId", query = "SELECT c FROM CourseSelectionOriginal c WHERE c.courseSelectionOriginalPK.courseId = :courseId")})
public class CourseSelectionOriginal implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CourseSelectionOriginalPK courseSelectionOriginalPK;
    @JoinColumn(name = "course_id", referencedColumnName = "auto_gen_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Course course;

    public CourseSelectionOriginal() {
    }

    public CourseSelectionOriginal(CourseSelectionOriginalPK courseSelectionOriginalPK) {
        this.courseSelectionOriginalPK = courseSelectionOriginalPK;
    }

    public CourseSelectionOriginal(int studentId, int courseId) {
        this.courseSelectionOriginalPK = new CourseSelectionOriginalPK(studentId, courseId);
    }

    public CourseSelectionOriginalPK getCourseSelectionOriginalPK() {
        return courseSelectionOriginalPK;
    }

    public void setCourseSelectionOriginalPK(CourseSelectionOriginalPK courseSelectionOriginalPK) {
        this.courseSelectionOriginalPK = courseSelectionOriginalPK;
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
        hash += (courseSelectionOriginalPK != null ? courseSelectionOriginalPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseSelectionOriginal)) {
            return false;
        }
        CourseSelectionOriginal other = (CourseSelectionOriginal) object;
        if ((this.courseSelectionOriginalPK == null && other.courseSelectionOriginalPK != null) || (this.courseSelectionOriginalPK != null && !this.courseSelectionOriginalPK.equals(other.courseSelectionOriginalPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseSelectionOriginal[ courseSelectionOriginalPK=" + courseSelectionOriginalPK + " ]";
    }
    
}
