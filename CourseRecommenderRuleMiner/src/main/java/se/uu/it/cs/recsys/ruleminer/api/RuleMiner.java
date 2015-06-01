package se.uu.it.cs.recsys.ruleminer.api;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.ruleminer.datastructure.FPTree;
import se.uu.it.cs.recsys.ruleminer.datastructure.builder.FPTreeBuilder;
import se.uu.it.cs.recsys.ruleminer.impl.FPGrowthImpl;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
public class RuleMiner {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleMiner.class);

    @Autowired
    private FPTreeBuilder treeBuilder;

    @Autowired
    private FPGrowthImpl fpGrowthImpl;

    /**
     *
     * @param targets
     * @param threshold
     * @return pairs of (rule, support)
     */
    @Cacheable("FPPatterns")
    public Map<Set<Integer>, Integer> getPatterns(Set<Integer> targets, Integer threshold) {


        Map<Set<Integer>, Integer> allRules = getPatterns(threshold);

        return allRules.entrySet()
                .stream()
                .filter(intertectWithTargets(targets))
                .filter(entry -> entry.getKey().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Cacheable("FPPatterns")
    public Map<Set<Integer>, Integer> getPatterns (Integer threshold) {
        
        int minSupport = threshold == null ? FPGrowthImpl.DEFAULT_MIN_SUPPORT : threshold;

        FPTree tree = treeBuilder.buildTreeFromDB(minSupport);

        if (tree == null) {
            LOGGER.debug("No FP tree can be built to meet the min support {} ",
                    minSupport);
            return Collections.emptyMap();
        }

        return fpGrowthImpl.getAllFrequentPattern(tree, minSupport);
    
    }

    private static Predicate<Map.Entry<Set<Integer>, Integer>> intertectWithTargets(Set<Integer> targets) {
        return entry -> hasIntersection(entry.getKey(), targets);
    }

    private static boolean hasIntersection(Set<Integer> a, Set<Integer> b) {
        Set<Integer> aClone = new HashSet<>(a);
        Set<Integer> bClone = new HashSet<>(b);

        aClone.retainAll(bClone);

        return !aClone.isEmpty();
    }
}
