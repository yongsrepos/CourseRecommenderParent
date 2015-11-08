package se.uu.it.cs.recsys.service.config;

/*
 * #%L
 * CourseRecommenderSpringMVC
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
import com.wordnik.swagger.jaxrs.config.BeanConfig;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
@ApplicationPath("/resources")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        packages(true, "se.uu.it.cs.recsys.service.resource", "com.fasterxml.jackson.jaxrs.json");

        register(com.wordnik.swagger.jaxrs.listing.ApiListingResource.class);
        register(com.wordnik.swagger.jaxrs.listing.SwaggerSerializers.class);
                
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setContact("Yong Huang (yong.e.huang@gmail.com)");
        beanConfig.setLicense("Apache 2.0");
        beanConfig.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");
        beanConfig.setTitle("Course Selection Service");
        beanConfig.setDescription("designed for Uppsala University Computer Science Master Program");
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/CourseRecommenderService/resources");
        beanConfig.setResourcePackage("se.uu.it.cs.recsys.service.resource");
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
    }

}
