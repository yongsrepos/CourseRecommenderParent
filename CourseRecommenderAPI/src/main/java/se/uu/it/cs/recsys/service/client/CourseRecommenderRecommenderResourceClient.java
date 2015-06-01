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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import se.uu.it.cs.recsys.api.type.Course;
import se.uu.it.cs.recsys.api.type.CourseSelectionPreference;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
public class CourseRecommenderRecommenderResourceClient {
    
    
    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("http://localhost:8080/CourseRecommenderService").path("/resources/recommender/recommend");

        registerJsonProviderTo(target);

        GenericType<Set<Set<Course>>> gt = new GenericType<Set<Set<Course>>>() {
        };
        
        CourseSelectionPreference pref = new CourseSelectionPreference();
        
        pref.setEnableRuleMining(true);
        pref.setEnalbeDomainReasoning(true);
        pref.setRecommendationAmount(5);
        pref.setMaxTotalCredits(100.0);
        pref.setMaxAdvancedCredits(75.0);
        pref.setMinFrequentItemSupport(15);
        
        Course exampleMandatoryCourse = new Course.Builder()
                .setCode("1DT032")
                .setName("Advanced Computer Science Studies in Sweden, 5 hp")
                .setTaughtYear(2015)
                .setStartPeriod(1)
                .build();
        
        pref.setMustTakeCourseCodeCollection(Stream.of(exampleMandatoryCourse)
                .collect(Collectors.toSet()));
        
        Set<String> courseWithDiffCode = Stream.of("1DL300", "1DL301")
                .collect(Collectors.toSet());
        pref.setAvoidMoreThanOneFromTheSameCollection(Stream.of(courseWithDiffCode).collect(Collectors.toSet()));
        
        pref.setInterestedComputingDomainCollection(
                Stream.of( "10003121","10010257","10002953","10011076", "10011074")
                        .collect(Collectors.toSet()));
        
        pref.setInterestedCourseCodeCollection(
                Stream.of(  "1DL450","1DL210","1DL360","1DL441","1DL301","1DL340","1TD186","1DL600","1DL400","1MD016","1TD480")
                        .collect(Collectors.toSet()));

        Set<Set<Course>> courses = target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pref, MediaType.APPLICATION_JSON), gt);

        courses.forEach(course -> System.out.println(course));
    }

    private static void registerJsonProviderTo(WebTarget target) {

        ServiceClientJsonProvider.getDefaultProvider()
                .forEach(providerClass -> target.register(providerClass));
    }
    
}
