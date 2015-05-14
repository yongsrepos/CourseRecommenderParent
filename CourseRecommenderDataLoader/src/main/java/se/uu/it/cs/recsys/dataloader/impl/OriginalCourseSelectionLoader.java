
package se.uu.it.cs.recsys.dataloader.impl;

/*
 * #%L
 * CourseRecommenderDataLoader
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


import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.dataloader.util.FixedValueForPreviousYears;
import se.uu.it.cs.recsys.persistence.entity.CourseSelectionOriginal;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.repository.CourseSelectionOriginalRepository;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class OriginalCourseSelectionLoader extends CourseSelectionLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(OriginalCourseSelectionLoader.class);

    @Autowired
    private CourseSelectionOriginalRepository courseSelectionOriginalRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void loadToDB() throws DataSourceFileAccessException {
        try {
            parseCourseSelectionRecordFiles();
        } catch (IOException ex) {
            throw new DataSourceFileAccessException("Failed loading course selection data", ex);
        }

        this.origCourseSelection.entrySet().forEach(entry -> loadToDB(entry));

    }

    private void loadToDB(Entry<String, Set<String>> singleSutdentSelection) {
        Integer studentId = Integer.valueOf(singleSutdentSelection.getKey());

        singleSutdentSelection.getValue().forEach(courseCode -> {
            Integer courseId = this.courseRepository.findByCodeAndTaughtYearAndStartPeriod(courseCode,
                    FixedValueForPreviousYears.COURSE_TAUGHT_YEAR.shortValue(),
                    FixedValueForPreviousYears.COURSE_START_PERIOD.shortValue()).getAutoGenId();

            this.courseSelectionOriginalRepository.save(new CourseSelectionOriginal(studentId, courseId));
        });
    }

}
