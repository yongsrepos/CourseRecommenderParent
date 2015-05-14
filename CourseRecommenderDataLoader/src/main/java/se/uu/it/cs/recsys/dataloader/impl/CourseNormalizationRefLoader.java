
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.dataloader.DataLoader;
import se.uu.it.cs.recsys.persistence.entity.CourseNormalization;
import se.uu.it.cs.recsys.persistence.repository.CourseNormalizationRepository;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class CourseNormalizationRefLoader implements DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseNormalizationRefLoader.class);

    private static final String NORMALIZATION_FILE = "data_source/normalization_ref_including_shared.csv";

    @Value(NORMALIZATION_FILE)
    private Resource normalizationFile;

    @Autowired
    private CourseNormalizationRepository courseNormalizationRepository;

    private final Set<CourseNormalization> courseNormalizationItems = new HashSet<>();

    @Override
    public void loadToDB() throws DataSourceFileAccessException {
        if (this.courseNormalizationItems.isEmpty()) {
            parseFile();
        }

       writeToDB();
    }

    /**
     *
     * @return non-null set, with normalization info, e.g a course taught in
     * earlier year was given an different code or name in later year. Or a
     * course in precious year is split into two or more courses in later year.
     *
     * @throws DataSourceFileAccessException if exception happens loading or
     * processing the source file
     */
    public Set<CourseNormalization> getNormalizationItems() throws DataSourceFileAccessException {
        if (this.courseNormalizationItems.isEmpty()) {
            parseFile();
        }

        return this.courseNormalizationItems;
    }

    /**
     * Retrieves normalization info, e.g a course taught in earlier year was
     * given an different code or name in later year. Or a course in precious
     * year is split into two or more courses in later year. A pair (fromCode,
     * (toCodes)) is such normal info. A fromCode means the course code in
     * earlier year.
     *
     * @return non-null mapping between history course code to target academic
     * year course code.
     * @throws DataSourceFileAccessException if failed loading the normalization
     * file info
     */
    public Map<String, Set<String>> getNormalizationPairs() throws DataSourceFileAccessException {
        if (this.courseNormalizationItems.isEmpty()) {
            parseFile();
        }

        Map<String, Set<String>> r = new HashMap<>();

        this.courseNormalizationItems.forEach(item -> {
            if (r.containsKey(item.getCourseNormalizationPK().getFromEarlierCode())) {
                r.get(item.getCourseNormalizationPK().getFromEarlierCode())
                        .add(item.getCourseNormalizationPK().getToLaterCode());
            } else {
                Set<String> toSet = new HashSet<>();
                toSet.add(item.getCourseNormalizationPK().getToLaterCode());
                r.put(item.getCourseNormalizationPK().getFromEarlierCode(),
                        toSet);
            }
        }
        );

        return r;

    }

    private void parseFile() throws DataSourceFileAccessException {
        if (!this.normalizationFile.exists()) {
            final String MSG = "Normalization file not exists at: " + NORMALIZATION_FILE;

            throw new DataSourceFileAccessException(MSG);
        }

        File sourceFile;
        try {
            sourceFile = this.normalizationFile.getFile();
        } catch (IOException ex) {
            final String MSG = "Failed loading normalization file at: " + NORMALIZATION_FILE;
            throw new DataSourceFileAccessException(MSG, ex);
        }

        try (FileReader fr = new FileReader(sourceFile); BufferedReader br = new BufferedReader(fr)) {
            br.lines().forEach(line -> {
                try {
                    CourseNormalization courseNormalization = parseLine(line);
                    this.courseNormalizationItems.add(courseNormalization);
                } catch (Exception e) {
                    LOGGER.error("Bad formmated line: {}", line, e);
                }
            });
        } catch (Exception e) {
            final String MSG = "Failed loading normalization file at: " + NORMALIZATION_FILE;
            throw new DataSourceFileAccessException(MSG, e);
        }
    }

    private CourseNormalization parseLine(String line) {
        String[] subStrs = line.split(";");

        if (subStrs.length < 2) {
            throw new IllegalArgumentException("Not well formatted line: " + line);
        }

        String fromCode = subStrs[0];
        String toCode = subStrs[2];

        return new CourseNormalization(fromCode, toCode);
    }

    private void writeToDB() {
        this.courseNormalizationRepository.save(this.courseNormalizationItems);
    }
}
