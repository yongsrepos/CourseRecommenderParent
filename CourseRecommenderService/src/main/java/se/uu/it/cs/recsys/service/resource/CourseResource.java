package se.uu.it.cs.recsys.service.resource;

/*
 * #%L
 * CourseRecommenderService
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
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.api.type.CourseCredit;
import se.uu.it.cs.recsys.api.type.CourseLevel;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseCreditRepository;
import se.uu.it.cs.recsys.persistence.repository.SupportedCourseLevelRepository;
import se.uu.it.cs.recsys.persistence.util.CourseConverter;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
@Path("/courses")
@Api(value = "courses", description = "Get course information.")
public class CourseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseResource.class);

    public static final Integer UU_FOUND_YEAR = 1477;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SupportedCourseLevelRepository supportedCourseLevelRepository;

    @Autowired
    private SupportedCourseCreditRepository supportedCourseCreditRepository;

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Ping-Pong", response = String.class)
    public String ping() {

        return "pong!";

    }

    @Path("/credits")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get supported course credits.",
            response = CourseCredit.class,
            responseContainer = "Set")
    public Set<Double> getSupportedCredits() {

        return this.supportedCourseCreditRepository.findAll()
                .stream()
                .map(entity -> entity.getCredit())
                .collect(Collectors.toSet());
    }

    @Path("/{code}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Query by course code",
            response = Course.class,
            responseContainer = "Set")
    public Set<Course> getCourse(@ApiParam(name = "code", required = true)
            @PathParam("code") String code) {

        return this.courseRepository
                .findByCode(code)
                .stream()
                .map(entity -> CourseConverter.convert(entity))
                .collect(Collectors.toSet());

    }

    @Path(value = "/levels")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get supported course Levels.",
            response = CourseLevel.class,
            responseContainer = "Set")
    public Set<CourseLevel> getSupportedCourseLevel() {

        return this.supportedCourseLevelRepository
                .findAll().stream()
                .map(entity -> CourseLevel.ofDBString(entity.getLevel()))
                .collect(Collectors.toSet());

    }

    @GET
    @Path("/year/{year}/period/start/{period}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Find course according to taught year and start period.",
            response = Course.class,
            responseContainer = "Set")
    public Response getCourseByScheduleInfo(
            @ApiParam(name = "year", required = true)
            @PathParam("year") Integer taughtYear,
            @ApiParam(name = "period", allowableValues = "1,2,3,4", required = true)
            @PathParam("period") Integer startPeriod
    ) {
        LOGGER.info("Get course taught in {}, started in period {}", taughtYear, startPeriod);

        if (taughtYear == null || taughtYear < UU_FOUND_YEAR) {

            LOGGER.error("Input year is {}, UU founded in {} ", taughtYear, UU_FOUND_YEAR);

            final String REASON = "UU founded yet in year " + UU_FOUND_YEAR;

            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Reason", REASON)
                    .build();
        }

        if (startPeriod == null) {
            final String REASON = "Start period is null";

            LOGGER.error(REASON);

            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Reason", REASON)
                    .build();
        }

        Set<Course> courses = this.courseRepository
                .findByTaughtYearAndStartPeriod(
                        taughtYear.shortValue(),
                        startPeriod.shortValue())
                .stream()
                .map(dbEntity -> CourseConverter.convert(dbEntity))
                .collect(Collectors.toSet());

        return Response.ok(courses, MediaType.APPLICATION_JSON).build();

    }

}
