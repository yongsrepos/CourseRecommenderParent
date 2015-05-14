
package se.uu.it.cs.recsys.dataloader.correction;

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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Class to correct the data in course selection record files. For example,
 * correct the course code or course name.
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class CourseSelectionRecourseCorrecter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseSelectionRecourseCorrecter.class);

    public static void main(String[] args) throws IOException {
        final String COURSE_SEL_REC_FILES_DIR = "file:C:\\Dev\\yong\\CourseRecommenderParent\\CourseRecommenderDataLoader\\src\\main\\resources\\data_source\\course_selection_records";

        Resource courseSelectionDir = new FileSystemResource(COURSE_SEL_REC_FILES_DIR);

        File[] recordFiles = courseSelectionDir.getFile().listFiles();

        Arrays.stream(recordFiles).forEach(file -> {
            try {
                correctCourseName(file, CourseNameCorrectionGenerator.getWrongToCorrectNamePairs());
            } catch (IOException ex) {
                LOGGER.error("Failed to correct names in file.{}", ex);
            }
        });
    }

    public static void correctCourseName(File file, Map<String, String> wrongToCorrect) throws IOException {
        List<String> processedLines = new ArrayList<>();

        try (FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)) {

            br.lines().forEach(line -> {
                String lineCopy = replaceWrongCourseNameWithCorrect(line, wrongToCorrect);
                processedLines.add(lineCopy);
            });
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            processedLines.stream()
                    .forEach(line -> pw.println(line));
        }
    }

    private static String replaceWrongCourseNameWithCorrect(String line, Map<String, String> wrongToCorrect) {
        StringBuilder lineCopy = new StringBuilder(line);

        wrongToCorrect.entrySet().stream().forEach(
                entry -> {
                    if (lineCopy.toString().contains(entry.getKey())) {

                        String contentCopy = lineCopy.toString();
                        String replacedCopy = contentCopy.replace(entry.getKey(), entry.getValue());

                        lineCopy.setLength(0);

                        lineCopy.append(replacedCopy);
                    }
                }
        );

        return lineCopy.toString();
    }

}
