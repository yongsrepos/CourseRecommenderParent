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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@XmlRootElement
public class Course {

    private static final Logger LOGGER = LoggerFactory.getLogger(Course.class);

    private String code;
    private String name;
    private Integer taughtYear;
    private Integer startPeriod;
    private Integer endPeriod;
    private Double credit;
    private CourseLevel level;
    private final Set<ComputingDomain> relatedDomain = new HashSet<>();

    private Course() {
        // no-arg, jaxb
    }

    public CourseLevel getLevel() {
        return level;
    }

    public String getCode() {
        return code;
    }

    /**
     * Retrieves the set of related domains to this course.
     *
     * @return non-null set
     */
    public Set<ComputingDomain> getRelatedDomain() {
        return relatedDomain;
    }

    /**
     * Adds related computing domain to this course.
     *
     * @param computingDomain a related computing domain. If null is input,
     * nothing will be added.
     * @return non-null set of related course.
     */
    public Set<ComputingDomain> addRelatedDomain(ComputingDomain computingDomain) {
        if (computingDomain == null) {
            LOGGER.warn("Nonsense to have null input here. Do you agree?");
            return this.relatedDomain;
        }

        this.relatedDomain.add(computingDomain);

        return this.relatedDomain;
    }

    public String getName() {
        return name;
    }

    public Integer getTaughtYear() {
        return taughtYear;
    }

    public Integer getStartPeriod() {
        return startPeriod;
    }

    public Integer getEndPeriod() {
        return endPeriod;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTaughtYear(Integer taughtYear) {
        this.taughtYear = taughtYear;
    }

    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    public void setEndPeriod(Integer endPeriod) {
        this.endPeriod = endPeriod;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public void setLevel(CourseLevel level) {
        this.level = level;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.code);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.taughtYear);
        hash = 53 * hash + Objects.hashCode(this.startPeriod);
        hash = 53 * hash + Objects.hashCode(this.endPeriod);
        hash = 53 * hash + Objects.hashCode(this.credit);
        hash = 53 * hash + Objects.hashCode(this.level);
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
        final Builder other = (Builder) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.taughtYear, other.taughtYear)) {
            return false;
        }
        if (!Objects.equals(this.startPeriod, other.startPeriod)) {
            return false;
        }
        if (!Objects.equals(this.endPeriod, other.endPeriod)) {
            return false;
        }
        if (!Objects.equals(this.credit, other.credit)) {
            return false;
        }
        if (this.level != other.level) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Builder{" + "code=" + code + ", name=" + name + ", taughtYear=" + taughtYear + ", startPeriod=" + startPeriod + ", endPeriod=" + endPeriod + ", credit=" + credit + ", level=" + level + '}';
    }

    public static class Builder {

        private String code;
        private String name;
        private Integer taughtYear;
        private Integer startPeriod;
        private Integer endPeriod;
        private Double credit;
        private CourseLevel level;

        public CourseLevel getLevel() {
            return level;
        }

        public Builder() {
            //no-arg
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTaughtYear(Integer taughtYear) {
            this.taughtYear = taughtYear;
            return this;
        }

        public Builder setStartPeriod(Integer startPeriod) {
            this.startPeriod = startPeriod;
            return this;
        }

        public Builder setEndPeriod(Integer endPeriod) {
            this.endPeriod = endPeriod;
            return this;
        }

        public Builder setCredit(Double credit) {
            this.credit = credit;
            return this;
        }

        public Builder setLevel(CourseLevel level) {
            this.level = level;
            return this;
        }

        public Course build() {
            if (this.code == null) {
                throw new IllegalStateException("Course code is not set!");
            }

            if (this.name == null) {
                throw new IllegalStateException("Course name is not set!");
            }

            if (this.taughtYear == null) {
                throw new IllegalStateException("Taught year is not set!");
            }

            if (this.startPeriod == null) {
                throw new IllegalStateException("Start period is not set!");
            }

            Course instance = new Course();
            instance.code = this.code;
            instance.credit = this.credit;
            instance.endPeriod = this.endPeriod;
            instance.name = this.name;
            instance.startPeriod = this.startPeriod;
            instance.taughtYear = this.taughtYear;
            instance.level = this.level;

            return instance;
        }

    }

}
