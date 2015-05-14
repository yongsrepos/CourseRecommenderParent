
package se.uu.it.cs.recsys.dataloader.util;

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


import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class HistoryCourseToCurrentNormalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryCourseToCurrentNormalizer.class);

    private static final String normalizationInfoFilePath = "classpath:data_source/history_course_normalized_to_plan_year.csv";

    @Value(normalizationInfoFilePath)
    private Resource historyNormalizationInfo;

    /**
     * Retrieves normalization info, e.g a course taught in earlier year was
     * given an different code or name in later year. A pair (fromCode, toCode)
     * is such normal info. A fromCode means the course code in earlier year.
     *
     * @return non-null mapping between history course code to target academic
     * year course code.
     * @throws java.io.FileNotFoundException if the normalization info file not
     * found
     * @throws java.io.IOException if any other IO exception happens
     */
    public Map<String, String> getNormalizationPairs() throws FileNotFoundException, IOException {
        File normalizationInfo;

        try {
            normalizationInfo = this.historyNormalizationInfo.getFile();
        } catch (IOException ex) {
            LOGGER.error("Can not load {}", normalizationInfoFilePath, ex);
            throw ex;
        }

        Map<String, String> result = Maps.newHashMap();

        try (
                FileReader fr = new FileReader(normalizationInfo);
                BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();

            while (line != null) {
                String[] subStrs = line.split(";");
                result.put(subStrs[0], subStrs[2]);
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            LOGGER.error("Can not find file {}", normalizationInfoFilePath, ex);
            throw ex;
        } catch (IOException ex) {
            LOGGER.error("Failed processing {}", normalizationInfoFilePath, ex);
            throw ex;
        }

        return result;
    }
}
