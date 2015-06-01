package se.uu.it.cs.recsys.constraint.util;

/*
 * #%L
 * CourseRecommenderConstraintSolver
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


import java.util.Comparator;
import se.uu.it.cs.recsys.api.type.Course;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseOrderComparator implements Comparator<Course> {

    @Override
    public int compare(Course o1, Course o2) {
        if (Integer.compare(o1.getTaughtYear(), o2.getTaughtYear()) != 0) {
            return Integer.compare(o1.getTaughtYear(), o2.getTaughtYear());
        } else {
            return Integer.compare(o1.getStartPeriod(), o2.getStartPeriod());
        }
    }

}
