
package se.uu.it.cs.recsys.ruleminer;

/*
 * #%L
 * CourseRecommenderRuleMiner
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.ruleminer.config.CourseRecommenderRuleMinerSpringConfig;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTree;
import se.uu.it.cs.recsys.ruleminer.datastructure.builder.FPTreeBuilder;
import se.uu.it.cs.recsys.ruleminer.impl.FPGrowthImpl;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class FPGrowthApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPGrowthApp.class);

    @Autowired
    private FPTreeBuilder treeBuilder;

    @Autowired
    private FPGrowthImpl ruleMiner;

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx
                = new AnnotationConfigApplicationContext(CourseRecommenderRuleMinerSpringConfig.class)) {

            FPGrowthApp app = ctx.getBean(FPGrowthApp.class);

            int minSupport = FPGrowthImpl.DEFAULT_MIN_SUPPORT;
            FPTree tree = app.treeBuilder.buildTreeFromDB(minSupport);

            if (tree == null) {
                LOGGER.debug("No FP tree can be built to meet the min support {} ", minSupport);
                return;
            }

            Map<Set<Integer>, Integer> patterns = app.ruleMiner.getAllFrequentPattern(tree);

            patterns.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().size() > 1)
                    .forEach(entry -> {
                        LOGGER.info("#### Pattern: {}, support: {}", entry.getKey(), entry.getValue());
                    });

        }
    }

}
