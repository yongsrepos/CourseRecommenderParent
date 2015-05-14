
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.api.exception.DatabaseAccessException;
import se.uu.it.cs.recsys.api.type.ComputingDomain;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.dataloader.DataLoader;
import se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevance;
import se.uu.it.cs.recsys.persistence.entity.CourseDomainRelevancePK;
import se.uu.it.cs.recsys.persistence.repository.CourseDomainRelevanceRepository;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseCreditRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseLevelRepository;

/**
 * Loads the course plan for the academical year.
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class FuturePlannedCourseLoader implements DataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(FuturePlannedCourseLoader.class);

    private static final String COURSE_PLAN_FILE_PATH = "classpath:data_source/course_plan.csv";

    @Value(COURSE_PLAN_FILE_PATH)
    private Resource coursePlan;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SupportedCourseCreditRepository supportedCourseCreditRepository;

    @Autowired
    private SupportedCourseLevelRepository supportedCourseLevelRepository;

    @Autowired
    private CourseDomainRelevanceRepository courseDomainRelevanceRepository;

    @Override
    public void loadToDB() throws DataSourceFileAccessException, DatabaseAccessException {
        if (!this.coursePlan.exists()) {
            final String msg = "Course plan source file not exist!";
            LOGGER.error(msg);
            throw new DataSourceFileAccessException(msg);
        }

        File planSrcFile;
        try {
            planSrcFile = this.coursePlan.getFile();
        } catch (IOException ex) {
            throw new DataSourceFileAccessException("Failed parsing course plan source file!", ex);
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

            se.uu.it.cs.recsys.persistence.entity.Course existing = this.courseRepository.findByCodeAndTaughtYearAndStartPeriod(entry.getCode(),
                    entry.getTaughtYear(),
                    entry.getStartPeriod());

            if (existing == null) {
                se.uu.it.cs.recsys.persistence.entity.Course saved = this.courseRepository.save(entry);

                writeMappingInfo(saved.getAutoGenId(), target);
            }
        });
    }

    private void writeMappingInfo(Integer courseId, Course course) {

        Set<se.uu.it.cs.recsys.api.type.ComputingDomain> relatedDomains
                = course.getRelatedDomain();

        relatedDomains.forEach(relatedDomain -> {
            CourseDomainRelevance newEntry = new CourseDomainRelevance(new CourseDomainRelevancePK(courseId, relatedDomain.getId()));

            this.courseDomainRelevanceRepository.save(newEntry);
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
            throw new DataSourceFileAccessException("Failed loaded course to domain relevance file info.", ex);
        }

        return records;
    }

    private Course convertToCourse(String line) {
        String[] subStrs = splitLine(line);

        Course course = new Course.Builder()
                .setCode(subStrs[0]).setName(subStrs[2]).setLevel(CourseLevel.ofHistoryCourseSelString(subStrs[1]))
                .setCredit(Double.parseDouble(subStrs[3])).setTaughtYear(Integer.parseInt(subStrs[5]))
                .setStartPeriod(Integer.parseInt(subStrs[6])).setEndPeriod(Integer.parseInt(subStrs[7]))
                .build();

        String[] relatedDomainIds = subStrs[4].split("\\.");

        Set<ComputingDomain> relatedDomains = new HashSet<>();

        for (String domainId : relatedDomainIds) {
            if(!domainId.isEmpty()){
                relatedDomains.add(new ComputingDomain(domainId));
            }            
        }

        if (!relatedDomains.isEmpty()) {
            course.getRelatedDomain().addAll(relatedDomains);
        }

        return course;
    }

    // e.g 1DL311;grund;Semantics of Programming Languages, 5 hp;5;10010134.10011039;2015;1;1
    private String[] splitLine(String line) {
        final String COMMA = ";";
        String[] subStrs = line.split(COMMA);
        return subStrs;
    }

}
