package se.uu.it.cs.recsys.constraint.solver;

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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.constraint.api.ConstraintSolverPreference;
import se.uu.it.cs.recsys.persistence.entity.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.util.CourseFlattener;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class ModelConfigBuilder {

    @Autowired
    private CourseRepository courseRepository;

    private ConstraintSolverPreference pref;

    private Set<Course> instreatedCourseCollection;

    public ModelConfig buildFrom(ConstraintSolverPreference pref) {
        ModelConfig instance = new ModelConfig();

        this.pref = pref;

        this.instreatedCourseCollection = this.courseRepository
                .findByAutoGenIds(this.pref.getInterestedCourseIdCollection());

        instance.setInterestedCourseIdCollection(this.pref
                .getInterestedCourseIdCollection());

        instance.setMaxAdvancedCredits(this.pref.getMaxAdvancedCredits());
        instance.setMaxTotalCredits(this.pref.getMaxTotalCredits());
        
        setMaxCourseAmount(instance);
        
        setAvoidCollectionForCourseWithDiffIdSet(instance);
        setCreditToAdvancedId(instance);
        setCreditToCourseId(instance);
        
        setPeriodIdxToMustHaveCourseId(instance);

        return instance;
    }

    private void setMaxCourseAmount(ModelConfig instance) {
        Integer maxCourseAmount = this.pref.getMaxCourseAmount();
        
        if(maxCourseAmount == null){
            instance.setMaxCourseAmount(ConstraintSolverPreference.MAX_TOTAL_COURSE_AMOUNT_DEFAULT);
        }else{
            instance.setMaxCourseAmount(this.pref.getMaxCourseAmount());
        }
    }

    private void setAvoidCollectionForCourseWithDiffIdSet(ModelConfig instance) {

        instance.setAvoidCollectionForCourseWithDiffIdSet(
                this.pref.getAvoidCollectionForCourseWithDiffIdSet());
    }

    private void setCreditToAdvancedId(ModelConfig instance) {
        Map<CourseCredit, Set<Integer>> map = new HashMap<>();

        Set<Course> interestedAdvancedCourseSet = this.instreatedCourseCollection
                .stream()
                .filter(course -> course.getLevel().getLevel()
                        .equals(CourseLevel.ADVANCED.getDBString()))
                .collect(Collectors.toSet());

        CourseFlattener
                .flattenToCreditAndIdSetMap(interestedAdvancedCourseSet)
                .entrySet()
                .stream()
                .forEach(entry -> {
                    map.put(
                            CourseCredit.ofValue(entry.getKey().getCredit().floatValue()),
                            entry.getValue());
                });

        instance.setCreditToAdvancedId(map);
    }

    private void setCreditToCourseId(ModelConfig instance) {

        Map<CourseCredit, Set<Integer>> map = new HashMap<>();

        CourseFlattener
                .flattenToCreditAndIdSetMap(this.instreatedCourseCollection)
                .entrySet()
                .stream()
                .forEach(entry -> {
                    map.put(
                            CourseCredit.ofValue(entry.getKey().getCredit().floatValue()),
                            entry.getValue());
                });

        instance.setCreditToCourseId(map);
    }

    private void setPeriodIdxToMustHaveCourseId(ModelConfig instance) {

        instance.setPeriodIdxToMustHaveCourseId(this.pref
                .getPeriodIdxToMustTakeCourseId());
    }

}
