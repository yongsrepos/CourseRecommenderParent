
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Entity
@Table(name = "course_normalization")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CourseNormalization.findAll", query = "SELECT c FROM CourseNormalization c"),
    @NamedQuery(name = "CourseNormalization.findByFromEarlierCode", query = "SELECT c FROM CourseNormalization c WHERE c.courseNormalizationPK.fromEarlierCode = :fromEarlierCode"),
    @NamedQuery(name = "CourseNormalization.findByToLaterCode", query = "SELECT c FROM CourseNormalization c WHERE c.courseNormalizationPK.toLaterCode = :toLaterCode")})
public class CourseNormalization implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CourseNormalizationPK courseNormalizationPK;

    public CourseNormalization() {
    }

    public CourseNormalization(CourseNormalizationPK courseNormalizationPK) {
        this.courseNormalizationPK = courseNormalizationPK;
    }

    public CourseNormalization(String fromEarlierCode, String toLaterCode) {
        this.courseNormalizationPK = new CourseNormalizationPK(fromEarlierCode, toLaterCode);
    }

    public CourseNormalizationPK getCourseNormalizationPK() {
        return courseNormalizationPK;
    }

    public void setCourseNormalizationPK(CourseNormalizationPK courseNormalizationPK) {
        this.courseNormalizationPK = courseNormalizationPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (courseNormalizationPK != null ? courseNormalizationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseNormalization)) {
            return false;
        }
        CourseNormalization other = (CourseNormalization) object;
        if ((this.courseNormalizationPK == null && other.courseNormalizationPK != null) || (this.courseNormalizationPK != null && !this.courseNormalizationPK.equals(other.courseNormalizationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseNormalization[ courseNormalizationPK=" + courseNormalizationPK + " ]";
    }
    
}
