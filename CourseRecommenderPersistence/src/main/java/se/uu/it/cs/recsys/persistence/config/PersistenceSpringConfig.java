package se.uu.it.cs.recsys.persistence.config;

/*
 * #%L
 * CourseRecommenderPersistence
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
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring-Data-JPA config file for this module. When using this module, import
 * this config class in your Application config class and also provide
 * application.properties, such as
 * <pre>
 * \@Import(PersistenceSpringConfig.class)
 * </pre>
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Configuration
@ComponentScan(basePackages = {"se.uu.it.cs.recsys.persistence"})
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("se.uu.it.cs.recsys.persistence.repository")
//@PropertySource("classpath:application.properties")
//@Import(SpringJpaRepositoryConfig.class)
public class PersistenceSpringConfig {

    @Bean
    public DataSource dataSource() {
        PoolProperties poolProperties = new PoolProperties();

        poolProperties.setUrl("jdbc:mysql://localhost:3306/uu_cs_course_recommender");
        poolProperties.setDriverClassName("com.mysql.jdbc.Driver");
        poolProperties.setUsername("root");
        poolProperties.setPassword("password");
        poolProperties.setJmxEnabled(true);
        poolProperties.setMaxActive(500);
        poolProperties.setLogAbandoned(true);

        DataSource tomcatDS = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
        
        return tomcatDS;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setGenerateDdl(true);

        return jpaVendorAdapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean lemfb = new LocalContainerEntityManagerFactoryBean();
        lemfb.setDataSource(dataSource());
        lemfb.setJpaVendorAdapter(jpaVendorAdapter());
        lemfb.setPackagesToScan("se.uu.it.cs.recsys.persistence.entity");
        return lemfb;
    }
}
