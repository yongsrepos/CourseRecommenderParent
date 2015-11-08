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
import se.uu.it.cs.recsys.service.resource.impl.RecommendationGenerator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.api.type.CourseSelectionPreference;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
@Path("/recommender")
@Api(value = "recommender", description = "Get course information.")
public class RecommenderResource {

    @Autowired
    private RecommendationGenerator recommendationGenerator;

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Ping-Pong")
    public String ping() {
        return "pong!";
    }

    @POST
    @Path("/recommend")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Generates recommendation based on preference",
            response = Course.class,
            consumes = MediaType.APPLICATION_JSON,
            responseContainer="Set"
    )
    public Response generateRcommendation(@ApiParam CourseSelectionPreference userPref) {

        Set<List<Course>> recommendations = this.recommendationGenerator
                .generateRcommendation(userPref);

        return Response.ok(recommendations, MediaType.APPLICATION_JSON).build();
    }

}
