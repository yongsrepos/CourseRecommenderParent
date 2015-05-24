package se.uu.it.cs.recsys.api.type;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

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
@XmlType
@XmlEnum(Double.class)
public enum CourseCredit {

    @XmlEnumValue("5.0f")
    FIVE(5.0f),
    @XmlEnumValue("7.5f")
    SEVEN_POINT_FIVE(7.5f),
    @XmlEnumValue("10.0f")
    TEN(10.0f),
    @XmlEnumValue("15.0f")
    FIVETEEN(15.0f),
    @XmlEnumValue("30.0f")
    THIRTY(30.0f);

    private final float credit;

    private CourseCredit(float credit) {
        this.credit = credit;
    }

    /**
     *
     * @param value, the input float value
     * @return matching instance or null;
     * @throws IllegalArgumentException if the input is not supported
     */
    public static CourseCredit ofValue(float value) {

        CourseCredit matching = null;

        for (CourseCredit courseCredit : CourseCredit.values()) {
            if (Float.compare(value, courseCredit.getCredit()) == 0) {
                matching = courseCredit;
            }
        }

        if (matching == null) {
            throw new IllegalArgumentException(value + " is not a valid course credit!");
        }

        return matching;
    }

    public float getCredit() {
        return this.credit;
    }

}
