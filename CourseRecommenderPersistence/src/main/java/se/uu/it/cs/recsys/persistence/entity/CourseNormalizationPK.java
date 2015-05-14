
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
public class CourseNormalizationPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "from_earlier_code")
    private String fromEarlierCode;
    @Basic(optional = false)
    @Column(name = "to_later_code")
    private String toLaterCode;

    public CourseNormalizationPK() {
    }

    public CourseNormalizationPK(String fromEarlierCode, String toLaterCode) {
        this.fromEarlierCode = fromEarlierCode;
        this.toLaterCode = toLaterCode;
    }

    public String getFromEarlierCode() {
        return fromEarlierCode;
    }

    public void setFromEarlierCode(String fromEarlierCode) {
        this.fromEarlierCode = fromEarlierCode;
    }

    public String getToLaterCode() {
        return toLaterCode;
    }

    public void setToLaterCode(String toLaterCode) {
        this.toLaterCode = toLaterCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fromEarlierCode != null ? fromEarlierCode.hashCode() : 0);
        hash += (toLaterCode != null ? toLaterCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CourseNormalizationPK)) {
            return false;
        }
        CourseNormalizationPK other = (CourseNormalizationPK) object;
        if ((this.fromEarlierCode == null && other.fromEarlierCode != null) || (this.fromEarlierCode != null && !this.fromEarlierCode.equals(other.fromEarlierCode))) {
            return false;
        }
        if ((this.toLaterCode == null && other.toLaterCode != null) || (this.toLaterCode != null && !this.toLaterCode.equals(other.toLaterCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.CourseNormalizationPK[ fromEarlierCode=" + fromEarlierCode + ", toLaterCode=" + toLaterCode + " ]";
    }
    
}
