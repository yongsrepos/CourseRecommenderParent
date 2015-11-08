//package se.uu.it.cs.recsys.service.config;
//
///*
// * #%L
// * CourseRecommenderSpringMVC
// * %%
// * Copyright (C) 2015 Yong Huang  <yong.e.huang@gmail.com >
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import org.springframework.core.annotation.Order;
//import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
//
///**
// *
// * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
// */
//@Order(value = 1)
//public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
//
//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        super.onStartup(servletContext);
//    }
//
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class[]{};
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class[]{CourseRecommenderServiceSpringConfig.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/","/swagger","/swagger-ui"};
//    }
//
//}
