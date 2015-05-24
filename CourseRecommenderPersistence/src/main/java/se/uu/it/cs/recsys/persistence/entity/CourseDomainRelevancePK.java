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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + courseId;
		result = prime * result
				+ ((domainId == null) ? 0 : domainId.hashCode());
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
		CourseDomainRelevancePK other = (CourseDomainRelevancePK) obj;
		if (courseId != other.courseId)
			return false;
		if (domainId == null) {
			if (other.domainId != null)
				return false;
		} else if (!domainId.equals(other.domainId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevancePK[ courseId="
				+ courseId + ", domainId=" + domainId + " ]";
	}

}
