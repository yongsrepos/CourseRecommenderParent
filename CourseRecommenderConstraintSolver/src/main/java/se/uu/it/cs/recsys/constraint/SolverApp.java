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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.constraint.config.ConstraintSolverSpringConfig;

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
                    .of(118, 119, 120, 121, 122, 123, 124, 125, 126, 127,
                            128, 129, 130, 131, 132, 133, 134, 135, 136, 137,
                            138, 139, 140, 141, 142, 143, 144, 145, 146, 147,
                            148, 149, 150, 151, 152, 153, 154, 155, 156, 157,
                            158, 159, 160, 161, 162, 163, 164, 165, 166, 167,
                            168, 169, 170, 171, 172, 173, 174, 175, 176, 177,
                            178, 179, 180, 181, 182, 183, 184, 185, 186, 187,
                            188, 189, 190, 191, 192, 193, 194, 195, 196, 197,
                            198, 199, 200, 201, 202, 203, 204, 205, 206, 207,
                            208, 209, 210, 211, 212, 213, 214, 215, 216, 217,
                            218, 219, 220, 221, 222, 223, 224, 225, 226, 227,
                            228, 229, 230, 231, 232, 233, 234, 235, 236, 237,
                            238, 239, 240, 241, 242, 243, 244, 245, 246, 247,
                            248, 249, 250, 251, 252, 253, 254, 255, 256, 257,
                            258, 259, 260, 261, 262, 263, 264, 265, 266, 267,
                            268, 269, 270, 271, 272, 273, 274, 275)
                    .collect(Collectors.toSet());

            Map<Integer, Set<Integer>> periodOneBasedIdxToMustHaveIdSet
                    = new HashMap<>();
            periodOneBasedIdxToMustHaveIdSet.put(1, Stream.of(118).collect(Collectors.toSet()));

            solver.addMandatoryPlan(periodOneBasedIdxToMustHaveIdSet);

            List<Map<Integer, Set<se.uu.it.cs.recsys.api.type.Course>>> solutions = solver.getSolutionWithPreference(interestedCourseIdSet);

            solutions.forEach(
                    solution -> {
                        LOGGER.info("==> Solution: ");
                        solution.entrySet().forEach(period -> {
                            LOGGER.info("Period {}, courses:", period.getKey());
                            period.getValue().forEach(course -> LOGGER.info("{}", course));
                        });
                    }
            );
        }
    }
}
