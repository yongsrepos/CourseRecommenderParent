
package se.uu.it.cs.recsys.semantic;

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


import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.uu.it.cs.recsys.semantic.config.ComputingDomainReasonerSpringConfig;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ComputingDomainReasonerSpringConfig.class})
public class ComputingDomainReasonerTest {

    public ComputingDomainReasonerTest() {
    }

    @Autowired
    private ComputingDomainReasoner reasoner;


    /**
     * Test of getIdAndLabel method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetIdAndLabel() throws Exception {
        Map<String, String> result = this.reasoner.getIdAndLabel();

        assertTrue(result.size() > 0);
    }

    /**
     * Test of getPrefLabel method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetPrefLabel() throws Exception {

        final String domainId = "10003350";
        final String expResult = "Recommender systems";
        final Optional<String> result = this.reasoner.getPrefLabel(domainId);
        assertEquals(expResult, result.get());

    }

    /**
     * Test of getPrefLabel method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetPrefLabel_No_Matching() throws Exception {

        final String domainId = "--10003350--";
        final Optional<String> result = this.reasoner.getPrefLabel(domainId);
        assertTrue(!result.isPresent());

    }

    /**
     * Test of getNarrowerDomainIds method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetNarrowerDomainIds() throws Exception {

        final Set<String> expResult = Stream.of("10003218", "10003269", "10003443", "10003444", "10003445", "10003446").
                collect(Collectors.toSet());

        final String dataMiningId = "10003351";

        Set<String> result = this.reasoner.getNarrowerDomainIds(dataMiningId);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNarrowerDomainIds method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetNarrowerDomainIds_Empty_Result() throws Exception {

        final String recommenderSystemId = "10003350";

        assertTrue(this.reasoner.getNarrowerDomainIds(recommenderSystemId).isEmpty());
    }

    /**
     * Test of getRelatedDomainIds method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetRelatedDomainIds() throws Exception {

        final String recommenderSystemId = "10003350";

        final Set<String> expNonEmpty = Stream.of("10003269").
                collect(Collectors.toSet());

        Set<String> result = this.reasoner.getRelatedDomainIds(recommenderSystemId);
        assertEquals(expNonEmpty, result);
    }

    /**
     * Test of getRelatedDomainIds method, of class ComputingDomainReasoner.
     */
    @Test
    public void testGetRelatedDomainIds_Empty_Result() throws Exception {
        final String dataMiningId = "10003351";

        Set<String> result = this.reasoner.getRelatedDomainIds(dataMiningId);
        assertTrue(result.isEmpty());

    }

}
