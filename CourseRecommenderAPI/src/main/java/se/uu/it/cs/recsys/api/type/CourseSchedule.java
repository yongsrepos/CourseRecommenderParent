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
public class CourseSchedule {

    private short taughtYear;
    private short startPeriod;
    private short endPeriod;
    private short periodIdxAmongAllPlanPeriods;

    public short getPeriodIdxAmongAllPlanPeriods() {
        return periodIdxAmongAllPlanPeriods;
    }

    public void setPeriodIdxAmongAllPlanPeriods(short periodIdxAmongAllPlanPeriods) {
        this.periodIdxAmongAllPlanPeriods = periodIdxAmongAllPlanPeriods;
    }

    public CourseSchedule(short taughtYear, short startPeriod) {
        this.taughtYear = taughtYear;
        this.startPeriod = startPeriod;
    }
    
    public CourseSchedule(short taughtYear, short startPeriod, short periodIdxAmongAllPlanPeriods) {
        this.taughtYear = taughtYear;
        this.startPeriod = startPeriod;
        this.periodIdxAmongAllPlanPeriods = periodIdxAmongAllPlanPeriods;
    }

    public short getTaughtYear() {
        return taughtYear;
    }

    public void setTaughtYear(short taughtYear) {
        this.taughtYear = taughtYear;
    }

    public short getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(short startPeriod) {
        this.startPeriod = startPeriod;
    }

    public short getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(short endPeriod) {
        this.endPeriod = endPeriod;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.taughtYear;
        hash = 83 * hash + this.startPeriod;
        hash = 83 * hash + this.endPeriod;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CourseSchedule other = (CourseSchedule) obj;
        if (this.taughtYear != other.taughtYear) {
            return false;
        }
        if (this.startPeriod != other.startPeriod) {
            return false;
        }
        if (this.endPeriod != other.endPeriod) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CourseSchedule{" + "taughtYear=" + taughtYear + ", startPeriod=" + startPeriod + ", endPeriod=" + endPeriod + '}';
    }

}
