package se.uu.it.cs.recsys.api.type;

/*
 * #%L
 * CourseRecommenderAPI
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
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@XmlRootElement
public class ComputingDomain {

    private String id;
    private String name;

    private ComputingDomain() {
        // for jaxb
    }

    public ComputingDomain(String id) {
    	this();
    	
        this.id = id;
        this.name = null;
    }

    /**
     *
     * @param id non-null, non-empty
     * @param name non-null,non-empty
     * @throws IllegalArgumentException if any input is null or empty
     */
    public ComputingDomain(String id, String name) {
        if (id == null || id.isEmpty() || name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Nonsense to call with null or empty string. do you agree?");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    /**
     *
     *
     * @param name non-null,non-empty
     * @throws IllegalArgumentException if any input is null or empty
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Nonsense to call with null or empty string. do you agree?");
        }
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComputingDomain other = (ComputingDomain) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "ComputingDomain{" + "id=" + id + ", name=" + name + '}';
    }
}
