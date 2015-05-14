
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


import se.uu.it.cs.recsys.dataloader.correction.CourseSelectionRecourseCorrecter;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class CourseSelectionRecourseCorrecterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseSelectionRecourseCorrecterTest.class);

    public CourseSelectionRecourseCorrecterTest() {
    }

    @Test
    public void testCorrectCourseName() throws Exception {
        Path tmp = Files.createTempFile("CourseSelectionRecourseCorrecterTest" + System.currentTimeMillis(), ".tmp");

        LOGGER.info("Temp test file created. Will be deleted automatically. : {}", tmp);

        final String lineOnePrefix = "This is line one prefix ";
        final String lineTwoPrefix = "This is line two prefix ";

        final String wrongName = "wrong name";
        final String correctName = "correct name";

        Map<String, String> wrongToCorrect = Maps.newHashMap();
        wrongToCorrect.put(wrongName, correctName);

        final String lineOneWithWrongName = lineOnePrefix + wrongName;
        final String lineOneWithCorrectName = lineOnePrefix + correctName;

        final String lineTwoWithCorrectName = lineTwoPrefix + correctName;

        try (PrintWriter pw = new PrintWriter(tmp.toFile())) {
            pw.println(lineOneWithWrongName);
            pw.println(lineTwoWithCorrectName);
        }

        try (FileReader fr = new FileReader(tmp.toFile()); BufferedReader br = new BufferedReader(fr)) {
            Assert.assertEquals(lineOneWithWrongName, br.readLine());
            Assert.assertEquals(lineTwoWithCorrectName, br.readLine());
        }

        CourseSelectionRecourseCorrecter.correctCourseName(tmp.toFile(), wrongToCorrect);

        try (FileReader fr = new FileReader(tmp.toFile()); BufferedReader br = new BufferedReader(fr)) {
            Assert.assertEquals(lineOneWithCorrectName, br.readLine());
            Assert.assertTrue(lineTwoWithCorrectName.equals(br.readLine()));
        }

        tmp.toFile().deleteOnExit();
    }

}
