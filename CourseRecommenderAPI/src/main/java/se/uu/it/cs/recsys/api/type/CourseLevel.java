
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
public enum CourseLevel {

    ADVANCED("avanc"), BASIC("grund");
    private String historyCourseSelString;

    CourseLevel(String origStr) {
        this.historyCourseSelString = origStr;
    }

    public static CourseLevel ofDBString(String valueInDB) {
        switch (valueInDB) {
            case "ADVANCED":
                return ADVANCED;
            case "BASIC":
                return BASIC;
            default:
                throw new IllegalArgumentException("Not suppoerted input: " + valueInDB);
        }
    }

    public static CourseLevel ofHistoryCourseSelString(String str) {
        switch (str) {
            case "avanc":
                return ADVANCED;
            case "grund":
                return BASIC;
            default:
                throw new IllegalArgumentException("Not suppoerted input: " + str);
        }
    }

    public String getHistoryCourseSelString() {
        return this.historyCourseSelString;
    }
    
    public String getDBString(){
        return this.name();
    }

}
