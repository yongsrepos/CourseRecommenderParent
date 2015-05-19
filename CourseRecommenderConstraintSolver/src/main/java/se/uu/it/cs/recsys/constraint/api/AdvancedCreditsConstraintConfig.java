package se.uu.it.cs.recsys.constraint.api;

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


/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class AdvancedCreditsConstraintConfig {

    public static final int MIN_ADVANCED_CREDIT_DEFAULT = 60;
    public static final int MAX_ADVANCED_CREDIT_DEFAULT 
            = TotalCreditsConstraintConfig.MAX_TOTAL_CREDIT_DEFAULT;

    private int minAdvancedCredits;
    private int maxAdvancedCredits;

    private AdvancedCreditsConstraintConfig() {
        // no-arg
    }

    private AdvancedCreditsConstraintConfig(int min, int max) {
        this.minAdvancedCredits = min;
        this.maxAdvancedCredits = max;
    }

    public static AdvancedCreditsConstraintConfig getInstanceWithDefaultValues() {

        return new AdvancedCreditsConstraintConfig(MIN_ADVANCED_CREDIT_DEFAULT,
                MAX_ADVANCED_CREDIT_DEFAULT);

    }

    public static AdvancedCreditsConstraintConfig
            getInstanceWithDefaultMinAndInputAsMax(int maxAdvancedCredits) {

        return new AdvancedCreditsConstraintConfig(MIN_ADVANCED_CREDIT_DEFAULT,
                maxAdvancedCredits);
        
    }

    public int getMinAdvancedCredits() {
        return minAdvancedCredits;
    }

    public int getMaxAdvancedCredits() {
        return maxAdvancedCredits;
    }

    public void setMaxAdvancedCredits(int maxAdvancedCredits) {
        this.maxAdvancedCredits = maxAdvancedCredits;
    }

}
