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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.persistence.repository.CourseRepository;
import se.uu.it.cs.recsys.persistence.util.CourseConverter;
import se.uu.it.cs.recsys.ruleminer.api.RuleMiner;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
@Path("/patterns")
@Api(value = "patterns", description = "Find the frequent patterns.")
public class FrequenPatternResource {

    @Autowired
    private RuleMiner ruleMiner;

    @Autowired
    private CourseRepository courseRepository;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "all", notes = "list all frequent patterns", responseContainer = "Map")
    public Response listAllFrequentPatterns(@QueryParam("minSupport") Integer minSupport) {

        Map<Set<Integer>, Integer> patterns = this.ruleMiner.getPatterns(minSupport);

        Map<Set<Course>, Integer> output = convertToCourse(patterns);

        return Response.ok(output, MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "find", notes = "list all frequent patterns", responseContainer = "Map")
    public Response listAllFrequentPatterns(@QueryParam("codes") Set<String> codes,
            @QueryParam("minSupport") Integer minSupport) {

        if (codes == null || codes.isEmpty()) {
            return Response.ok().build();
        }

        Set<Integer> courseIds = new HashSet<>();

        codes.forEach(code -> {
            Set<se.uu.it.cs.recsys.persistence.entity.Course> courses
                    = this.courseRepository.findByCode(code);
            Set<Integer> ids = courses.stream()
                    .map(entry -> entry.getAutoGenId())
                    .collect(Collectors.toSet());

            courseIds.addAll(ids);
        });

        Map<Set<Integer>, Integer> patterns = this.ruleMiner.getPatterns(courseIds, minSupport);

        Map<Set<Course>, Integer> output = convertToCourse(patterns);

        return Response.ok(output, MediaType.APPLICATION_JSON).build();

    }

    private Map<Set<Course>, Integer> convertToCourse(Map<Set<Integer>, Integer> patterns) {

        Map<Set<Course>, Integer> output = new HashMap<>();

        patterns.forEach((k, v) -> {
            Set<se.uu.it.cs.recsys.persistence.entity.Course> courses
                    = this.courseRepository.findByAutoGenIds(k);

            Set<Course> apiCourses = new HashSet<>();

            courses.forEach(entity -> apiCourses.add(CourseConverter.convert(entity)));

            output.put(apiCourses, v);

        });

        return output;
    }

}
