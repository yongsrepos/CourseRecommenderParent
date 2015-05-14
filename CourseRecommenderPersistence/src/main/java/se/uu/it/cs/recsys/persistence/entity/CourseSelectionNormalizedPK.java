
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
public class CourseSelectionNormalizedPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "student_id")
    private int studentId;
    @Basic(optional = false)
    @Column(name = "normalized_course_id")
    private int normalizedCourseId;

    public CourseSelectionNormalizedPK() {
    }

    public CourseSelectionNormalizedPK(int studentId, int normalizedCourseId) {
        this.studentId = studentId;
        this.normalizedCourseId = normalizedCourseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getNormalizedCourseId() {
        return normalizedCourseId;
    }

    public void setNormalizedCourseId(int normalizedCourseId) {
        this.normalizedCourseId = normalizedCourseId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) studentId;
        hash += (int) normalizedCourseId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseSelectionNormalizedPK)) {
            return false;
        }
        CourseSelectionNormalizedPK other = (CourseSelectionNormalizedPK) object;
        if (this.studentId != other.studentId) {
            return false;
        }
        if (this.normalizedCourseId != other.normalizedCourseId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseSelectionNormalizedPK[ studentId=" + studentId + ", normalizedCourseId=" + normalizedCourseId + " ]";
    }
    
}
