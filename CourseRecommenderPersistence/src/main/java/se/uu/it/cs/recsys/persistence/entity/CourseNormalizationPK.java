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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromEarlierCode == null) ? 0 : fromEarlierCode.hashCode());
		result = prime * result
				+ ((toLaterCode == null) ? 0 : toLaterCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseNormalizationPK other = (CourseNormalizationPK) obj;
		if (fromEarlierCode == null) {
			if (other.fromEarlierCode != null)
				return false;
		} else if (!fromEarlierCode.equals(other.fromEarlierCode))
			return false;
		if (toLaterCode == null) {
			if (other.toLaterCode != null)
				return false;
		} else if (!toLaterCode.equals(other.toLaterCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "se.uu.it.cs.recsys.persistence.entity.CourseNormalizationPK[ fromEarlierCode="
				+ fromEarlierCode + ", toLaterCode=" + toLaterCode + " ]";
	}

}
