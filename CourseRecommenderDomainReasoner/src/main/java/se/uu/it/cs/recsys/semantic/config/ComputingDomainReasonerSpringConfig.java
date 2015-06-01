package se.uu.it.cs.recsys.semantic.config;

/*
 * #%L
 * CourseRecommenderDomainReasoner
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
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Configuration
@ComponentScan(basePackages = {"se.uu.it.cs.recsys.semantic"})
public class ComputingDomainReasonerSpringConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        GuavaCache fpTreeCache = new GuavaCache("ComputingDomainCache",
                CacheBuilder.
                newBuilder().
                expireAfterWrite(1, TimeUnit.DAYS).
                maximumSize(100).build());

        cacheManager.setCaches(Stream.of(fpTreeCache).collect(Collectors.toList()));

        return cacheManager;
    }

}
