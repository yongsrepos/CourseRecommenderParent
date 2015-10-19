package se.uu.it.cs.recsys.service.client;

/*
 * #%L
 * CourseRecommenderAPI
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
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.uu.it.cs.recsys.api.type.Course;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseRecommenderCourseResourceClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseRecommenderCourseResourceClient.class);

    public static void main(String[] args) {
        CourseRecommenderCourseResourceClient theClient = new CourseRecommenderCourseResourceClient();
        
        Set<Course> courses = theClient.getCourseByYearAndPeriod(2015, 1);
        
        courses.forEach(course->LOGGER.info("{}", course));
    }
    
    public Set<Course> getCourseByYearAndPeriod(Integer year, Integer period){
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("http://localhost:8080/CourseRecommenderService")
                .path("/resources/courses/year/")
                .path(year.toString())
                .path("/period/start/" + period.toString());

        registerJsonProviderTo(target);

        GenericType<Set<Course>> gt = new GenericType<Set<Course>>() {};

        return target.request(MediaType.APPLICATION_JSON).get(gt);
    }

    private static void registerJsonProviderTo(WebTarget target) {

        ServiceClientJsonProvider.getDefaultProvider()
                .forEach(providerClass -> target.register(providerClass));
    }

}
