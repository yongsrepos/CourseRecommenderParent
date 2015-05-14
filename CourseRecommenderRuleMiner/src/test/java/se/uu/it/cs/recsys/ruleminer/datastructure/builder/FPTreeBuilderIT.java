/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.uu.it.cs.recsys.ruleminer.datastructure.builder;

/*
 * #%L
 * CourseRecommenderRuleMiner
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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.uu.it.cs.recsys.ruleminer.config.CourseRecommenderRuleMinerTestSpringConfig;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CourseRecommenderRuleMinerTestSpringConfig.class)
public class FPTreeBuilderIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(FPTreeBuilderIT.class);

    @Autowired
    private FPTreeBuilder fpTreeBuilder;

    public FPTreeBuilderIT() {
    }

    @Test
    public void testBuildTreeFromDB() {
        long start = System.currentTimeMillis();
        
        this.fpTreeBuilder.buildTreeFromDB(20);

        long end = System.currentTimeMillis();
                
        LOGGER.info("****** Built time {} ms! ",end-start );
    }

//    @Test
//    public void testBuildTreeBodyFromDB() {
//    }
//
//    @Test
//    public void testInsertTransData() {
//    }
}
