
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
    @NamedQuery(name = "ComputingDomain.findById", query = "SELECT c FROM ComputingDomain c WHERE c.id = :id"),
    @NamedQuery(name = "ComputingDomain.findByName", query = "SELECT c FROM ComputingDomain c WHERE c.name = :name")})
public class ComputingDomain implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "computingDomain")
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
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ComputingDomain)) {
            return false;
        }
        ComputingDomain other = (ComputingDomain) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "se.uu.it.cs.recsys.persistence.entity.ComputingDomain[ id=" + id + " ]";
    }
    
}
