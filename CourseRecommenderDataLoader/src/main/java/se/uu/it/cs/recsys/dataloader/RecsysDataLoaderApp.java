package se.uu.it.cs.recsys.dataloader;

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
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.api.exception.DatabaseAccessException;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.dataloader.config.RecsysDataLoaderSpringConfig;
import se.uu.it.cs.recsys.dataloader.impl.ComputingDomainTaxonomyLoader;
import se.uu.it.cs.recsys.dataloader.impl.CourseNormalizationRefLoader;
import se.uu.it.cs.recsys.dataloader.impl.FuturePlannedCourseLoader;
import se.uu.it.cs.recsys.dataloader.impl.NormalizedCourseSelectionLoader;
import se.uu.it.cs.recsys.dataloader.impl.OriginalCourseSelectionLoader;
import se.uu.it.cs.recsys.dataloader.impl.PreviousYearsCourseLoader;
import se.uu.it.cs.recsys.persistence.config.PersistenceSpringConfig;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseCredit;
import se.uu.it.cs.recsys.persistence.entity.SupportedCourseLevel;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseCreditRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseLevelRepository;
import se.uu.it.cs.recsys.semantic.config.ComputingDomainReasonerSpringConfig;

/**
 *
 */
@Component
public class RecsysDataLoaderApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecsysDataLoaderApp.class);

    @Autowired
    private SupportedCourseCreditRepository supportedCourseCreditRepository;

    @Autowired
    private SupportedCourseLevelRepository supportedCourseLevelRepository;

    @Autowired
    private FuturePlannedCourseLoader futurePlannedCourseLoader;

    @Autowired
    private PreviousYearsCourseLoader previousYearsCourseLoader;

    @Autowired
    private CourseNormalizationRefLoader courseNormalizationRefLoader;

    @Autowired
    private ComputingDomainTaxonomyLoader computingDomainTaxonomyLoader;

    @Autowired
    private OriginalCourseSelectionLoader originalCourseSelectionLoader;

    @Autowired
    private NormalizedCourseSelectionLoader normalizedCourseSelectionLoader;

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext ctx
                = new AnnotationConfigApplicationContext(RecsysDataLoaderSpringConfig.class,
                        PersistenceSpringConfig.class,
                        ComputingDomainReasonerSpringConfig.class)) {
            RecsysDataLoaderApp dataLoader = ctx.getBean(RecsysDataLoaderApp.class);
            dataLoader.loadData();
        } catch (IOException ex) {
            LOGGER.error("Failed loading data!", ex);
        }
    }

    public void loadData() throws IOException {

        try {
            loadSupportedCourseCredit();

            loadSupportedCourseLevel();

            loadComputingDomainTaxonomyData();

            loadPreviousTaughtCourses();

            loadFuturePlannedCourse();

            loadOriginalCourseSelectionData();

            loadCourseNormalizationRefData();

            loadNormalizedCourseSelectionData();

        } catch (DatabaseAccessException | DataSourceFileAccessException ex) {
            java.util.logging.Logger.getLogger(RecsysDataLoaderApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadSupportedCourseCredit() {
        LOGGER.info("Loading supported course credit!");

        for (CourseCredit credit : CourseCredit.values()) {
            this.supportedCourseCreditRepository.save(new SupportedCourseCredit((double) credit.getCredit()));
        }
    }

    private void loadSupportedCourseLevel() {
        LOGGER.info("Loading supported course level!");

        for (CourseLevel level : CourseLevel.values()) {
            this.supportedCourseLevelRepository.save(new SupportedCourseLevel(level.getDBString()));
        }

    }

    private void loadFuturePlannedCourse() throws DataSourceFileAccessException, DatabaseAccessException {
        LOGGER.info("Loading planned courses for coming academic year!");

        this.futurePlannedCourseLoader.loadToDB();
    }

    private void loadPreviousTaughtCourses() throws DatabaseAccessException, DataSourceFileAccessException {
        LOGGER.info("Loading previously taught courses!");

        this.previousYearsCourseLoader.loadToDB();
    }

    private void loadComputingDomainTaxonomyData() throws DatabaseAccessException, DataSourceFileAccessException {
        LOGGER.info("Loading computing domain taxonomy data!");

        this.computingDomainTaxonomyLoader.loadToDB();
    }

    private void loadOriginalCourseSelectionData() throws DataSourceFileAccessException {
        LOGGER.info("Loading course selection data from previous years!");

        this.originalCourseSelectionLoader.loadToDB();

    }

    private void loadNormalizedCourseSelectionData() throws DatabaseAccessException, DataSourceFileAccessException {
        LOGGER.info("Loading normalized course selection data from previous years!");

        this.normalizedCourseSelectionLoader.loadToDB();

    }

    private void loadCourseNormalizationRefData() throws DataSourceFileAccessException {
        LOGGER.info("Loading course normalization reference data!");

        this.courseNormalizationRefLoader.loadToDB();
    }

}
