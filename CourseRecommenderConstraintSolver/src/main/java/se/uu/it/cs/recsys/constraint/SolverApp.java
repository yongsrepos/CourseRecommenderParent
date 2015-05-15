package se.uu.it.cs.recsys.constraint;

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jacop.set.core.SetVar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.constraint.config.ConstraintSolverSpringConfig;
import se.uu.it.cs.recsys.constraint.util.ConstraintResultConverter;

/**
 *
 */
@Component
public class SolverApp {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SolverApp.class);
    
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx
                = new AnnotationConfigApplicationContext(ConstraintSolverSpringConfig.class)) {
            Solver solver = ctx.getBean(Solver.class);
            
            Set<Integer> interestedCourseIdSet = Stream
                    .of(118, 119, 120, 121, 122, 123, 124, 125,
                            126, 127, 128, 129, 130, 131, 132, 133,
                            134, 135, 136, 137, 138, 139, 140, 141,
                            142, 143, 144, 145, 146, 147, 148, 149,
                            150, 151, 152, 153, 154, 155, 156, 157,
                            158, 159, 160, 161, 162, 163, 164, 165,
                            166, 167, 168, 169, 170, 171, 172, 173,
                            174, 175, 176, 177, 178, 179, 180, 181,
                            182, 183, 184, 185, 186, 187, 188, 189,
                            190, 191, 192, 193, 194, 195, 196, 197,
                            198, 199, 200, 201, 202, 203, 204, 205,
                            206, 207, 208, 209, 210, 211, 212, 213,
                            214, 215, 216, 217)
                    .collect(Collectors.toSet());
            
            
            SetVar[] solution = solver.getSolutionWithPreference(interestedCourseIdSet);
            
            ConstraintResultConverter constraintResultConverter = ctx.getBean(ConstraintResultConverter.class);
            
            Map<Integer, Set<Course>> periodAndCourseNames = constraintResultConverter.convert(solution);
            
            periodAndCourseNames.entrySet().forEach(
                    entry -> {
                        LOGGER.info("Period {}, courses:", entry.getKey());
                        entry.getValue().forEach(course -> LOGGER.info("{}", course));
                    });
            
        }
        
    }
    
}
