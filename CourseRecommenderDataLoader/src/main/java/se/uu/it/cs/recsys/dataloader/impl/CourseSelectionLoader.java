
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.dataloader.DataLoader;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
abstract class CourseSelectionLoader implements DataLoader {

    private static final String COURSE_SEL_REC_FILES_DIR = "classpath:data_source/course_selection_records";

    @Value(COURSE_SEL_REC_FILES_DIR)
    protected Resource courseSelectionDir;

    // student id -> (set of attended courses' codes)
    protected Map<String, Set<String>> origCourseSelection = new HashMap<>();

    protected abstract Logger getLogger();

    protected void parseCourseSelectionRecordFiles() throws IOException, DataSourceFileAccessException {
        if (!this.courseSelectionDir.exists()) {
            final String MSG = "No info about course selection source dir!";
            getLogger().error(MSG);
            throw new DataSourceFileAccessException(MSG);
        }

        Arrays.stream(this.courseSelectionDir.getFile()
                .listFiles()).forEach(
                        file -> {
                            try {
                                parseFile(file);
                            } catch (IOException ex) {
                                getLogger().error("Failed processing file: {}", file.getName(), ex);
                            }
                        }
                );

    }

    private void parseFile(File file) throws IOException {

        try (FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)) {
            br.lines().forEach(line -> {
                String[] subStrs = parseLine(line);

                final String studentId = subStrs[0].trim();
                final String courseCode = subStrs[3].trim();

                if (!isValidCourseCode(courseCode)) {
                    getLogger().error("Invalid course code: {}, {}", line, file.getName());
                } else {
                    if (this.origCourseSelection.containsKey(studentId)) {
                        this.origCourseSelection.get(studentId).add(courseCode);
                    } else {
                        Set<String> courseSetCode = Stream.of(courseCode).collect(Collectors.toSet());

                        this.origCourseSelection.put(studentId, courseSetCode);
                    }
                }
            });
        }
    }

    // an example line: 
    // 1	20082	TDV2E	1DL025	Data Mining	20082	2009-04-18
    private String[] parseLine(String line) {
        final String TAB = "\\t";
        String[] subStrs = line.split(TAB);
        return subStrs;
    }

    private boolean isValidCourseCode(String courseCode) {
        final String COURSE_CODE_PATTERN = "\\d{1}[A-Z]{2}\\d{3}"; // e.g 1DL025
        Pattern ptn = Pattern.compile(COURSE_CODE_PATTERN);

        return ptn.matcher(courseCode).matches();
    }
}
