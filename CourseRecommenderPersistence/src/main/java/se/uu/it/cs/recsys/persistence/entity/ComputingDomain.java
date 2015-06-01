
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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "computing_domain")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ComputingDomain.findAll", query = "SELECT c FROM ComputingDomain c"),
    @NamedQuery(name = "ComputingDomain.findById", query = "SELECT c FROM ComputingDomain c "
            + " LEFT JOIN FETCH c.courseDomainRelevanceCollection WHERE c.id = :id"),
    @NamedQuery(name = "ComputingDomain.findByName", query = "SELECT c FROM ComputingDomain c WHERE c.name = :name")})
public class ComputingDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "computingDomain")
    private Collection<CourseDomainRelevance> courseDomainRelevanceCollection;

    public ComputingDomain() {
    }

    public ComputingDomain(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Collection<CourseDomainRelevance> getCourseDomainRelevanceCollection() {
        return courseDomainRelevanceCollection;
    }

    public void setCourseDomainRelevanceCollection(Collection<CourseDomainRelevance> courseDomainRelevanceCollection) {
        this.courseDomainRelevanceCollection = courseDomainRelevanceCollection;
    }


    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((courseDomainRelevanceCollection == null) ? 0
						: courseDomainRelevanceCollection.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ComputingDomain other = (ComputingDomain) obj;
		if (courseDomainRelevanceCollection == null) {
			if (other.courseDomainRelevanceCollection != null)
				return false;
		} else if (!courseDomainRelevanceCollection
				.equals(other.courseDomainRelevanceCollection))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.ComputingDomain[ id=" + id + " ]";
    }
    
}
