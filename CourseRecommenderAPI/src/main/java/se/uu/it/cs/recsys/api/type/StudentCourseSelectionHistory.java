
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


import com.google.common.collect.Sets;
import java.io.Serializable;
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
public class StudentCourseSelectionHistory implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCourseSelectionHistory.class);

    private String studentId;
    private final Set<Course> courseSet = Sets.newHashSet();

    private StudentCourseSelectionHistory() {
        // no-arg, for JAXB
    }

    private StudentCourseSelectionHistory(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    /**
     * Add course selection record to a student's selection history set.
     *
     * @param course the selected course. If null is input, nothing will be
     * added.
     * @return non-null set of courses that a student has selected.
     */
    public Set<Course> addCourseSelectionRecord(Course course) {
        if (course == null) {
            LOGGER.warn("Nonsense to have null input here. Do you agree?");
            return this.courseSet;
        }

        this.courseSet.add(course);

        return this.courseSet;
    }

    /**
     * Gets the list of course that a student has attended.
     *
     * @return non-null Set.
     */
    public Set<Course> getCourseSet() {
        return courseSet;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.studentId);
        hash = 31 * hash + Objects.hashCode(this.courseSet);
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
        final StudentCourseSelectionHistory other = (StudentCourseSelectionHistory) obj;
        if (!Objects.equals(this.studentId, other.studentId)) {
            return false;
        }
        if (!Objects.equals(this.courseSet, other.courseSet)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StudentCourseSelectionHistory{" + "studentId=" + studentId + ", courseList=" + courseSet + '}';
    }

}
