
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


import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.api.exception.DatabaseAccessException;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.dataloader.DataLoader;
import se.uu.it.cs.recsys.dataloader.util.FixedValueForPreviousYears;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseCreditRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseLevelRepository;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class PreviousYearsCourseLoader implements DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreviousYearsCourseLoader.class);

    private static final String PREVIOUS_TAUGHT_COURSE_FILE_PATH = "classpath:data_source/history_course_id_and_name.csv";

    @Value(PREVIOUS_TAUGHT_COURSE_FILE_PATH)
    private Resource previousTaughtCourses;

    @Autowired
    private SupportedCourseCreditRepository supportedCourseCreditRepository;

    @Autowired
    private SupportedCourseLevelRepository supportedCourseLevelRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void loadToDB() throws DatabaseAccessException, DataSourceFileAccessException {
        if (!this.previousTaughtCourses.exists()) {
            final String msg = "Previous taught course file not exist!";
            LOGGER.error(msg);
            throw new DataSourceFileAccessException(msg);
        }

        File planSrcFile;
        try {
            planSrcFile = this.previousTaughtCourses.getFile();
        } catch (IOException ex) {
            throw new DataSourceFileAccessException("Failed parsing previous taught course file!", ex);
        }

        List<Course> allCourses = parseFile(planSrcFile);

        loadCoursesToDB(allCourses);

    }

    private void loadCoursesToDB(List<Course> targets) {

        targets.forEach(target -> {
            se.uu.it.cs.recsys.persistence.entity.Course entry = new se.uu.it.cs.recsys.persistence.entity.Course();
            entry.setCode(target.getCode());
            entry.setName(target.getName());
            entry.setTaughtYear(target.getTaughtYear().shortValue());
            entry.setStartPeriod(target.getStartPeriod().shortValue());
            entry.setEndPeriod(target.getEndPeriod().shortValue());

            entry.setCredit(this.supportedCourseCreditRepository.findByCredit(target.getCredit()));
            entry.setLevel(this.supportedCourseLevelRepository.findByLevel(target.getLevel().toString()));

            se.uu.it.cs.recsys.persistence.entity.Course savedEntry = this.courseRepository.save(entry);

            if (savedEntry == null) {
                LOGGER.error("Failed to save course: {}", entry);
            }
        });
    }

    private List<Course> parseFile(File file) throws DataSourceFileAccessException {
        List<Course> records = Lists.newArrayList();

        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {

            br.lines().forEach(line -> {
                Course course = convertToCourse(line);
                records.add(course);
            });

        } catch (IOException ex) {
            throw new DataSourceFileAccessException("Failed loaded previous taught course file info.", ex);
        }

        return records;
    }

    private Course convertToCourse(String line) {
        String[] subStrs = splitLine(line);

        // fake some data for previous years
        return new Course.Builder()
                .setCode(subStrs[0]).setName(subStrs[1]).setLevel(FixedValueForPreviousYears.COURSE_LEVLE)
                .setCredit(FixedValueForPreviousYears.COURSE_CREDIT).setTaughtYear(FixedValueForPreviousYears.COURSE_TAUGHT_YEAR)
                .setStartPeriod(FixedValueForPreviousYears.COURSE_START_PERIOD).setEndPeriod(FixedValueForPreviousYears.COURSE_END_PERIOD)
                .build();
    }

    // e.g 1DL004;Software Engineering
    private String[] splitLine(String line) {
        final String COMMA = ";";
        String[] subStrs = line.split(COMMA);
        return subStrs;
    }

}
