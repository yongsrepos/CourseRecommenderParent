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
public class TotalCreditsConstraintConfig {

    public static final int MIN_TOTAL_CREDIT_DEFAULT = 90;
    public static final int MAX_TOTAL_CREDIT_DEFAULT = 120;

    private int minTotalCredits;
    private int maxTotalCredits;

    private TotalCreditsConstraintConfig() {
        // no-arg
    }

    private TotalCreditsConstraintConfig(int min, int max) {
        this.minTotalCredits = min;
        this.maxTotalCredits = max;
    }

    public static TotalCreditsConstraintConfig getInstanceWithDefaultValues() {

        return new TotalCreditsConstraintConfig(MIN_TOTAL_CREDIT_DEFAULT,
                MAX_TOTAL_CREDIT_DEFAULT);

    }

    public static TotalCreditsConstraintConfig
            getInstanceWithDefaultMinAndInputAsMax(int maxTotalCredits) {

        return new TotalCreditsConstraintConfig(MIN_TOTAL_CREDIT_DEFAULT,
                maxTotalCredits);

    }

    public int getMinTotalCredits() {
        return minTotalCredits;
    }

    public int getMaxTotalCredits() {
        return maxTotalCredits;
    }

    public void setMaxTotalCredits(int maxTotalCredits) {
        this.maxTotalCredits = maxTotalCredits;
    }

}
