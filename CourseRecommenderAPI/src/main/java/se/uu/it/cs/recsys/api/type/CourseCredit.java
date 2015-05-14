
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


/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public enum CourseCredit {

    FIVE(5), SEVEN(7), TEN(10), FIVETEEN(15), THIRTY(30);

    private final int credit;

    private CourseCredit(int credit) {
        this.credit = credit;
    }
    
    public static CourseCredit ofValue(int value){
        switch(value){
            case 5: return FIVE;
            case 7: return SEVEN;
            case 10: return TEN;
            case 15: return FIVETEEN;
            case 30: return THIRTY;
            default:
                throw new IllegalArgumentException("Not supported value: "+ value);
        }
    }

    public int getCredit() {
        return this.credit;
    }

}
