
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


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.semantic.config.ComputingDomainReasonerSpringConfig;

/**
 * Can be used both and POJO and Spring Bean. When used as Spring bean, import
 * configuration {@link ComputingDomainReasonerSpringConfig} with Spring
 * {@link Import}.
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class ComputingDomainReasoner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputingDomainReasoner.class);

    private static final String TAXONOMY_PATH = "classpath:taxonomy/ACMComputingClassificationSystemSKOSTaxonomy.xml";

    @Value(TAXONOMY_PATH)
    private Resource skosFile;

    /**
     * Get id and label pairs.
     *
     * @return non-null set
     * @throws java.io.IOException
     */
    @Cacheable
    public Map<String, String> getIdAndLabel() throws IOException {

        Query query = SparqlQueryFactory.getIdAndPrefLabelQuery();
        Model model = getPopulatedModel();

        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            return SparqlResultParser.parseIdAndPrefLabelQueryResult(qe.execSelect());
        }
    }

    /**
     *
     * @param domainId the id of the domain
     * @return non-null instance of {@link Optional}, which may or may not
     * contains the preferred label of the domain.
     * @throws java.io.IOException
     */
    public Optional<String> getPrefLabel(String domainId) throws IOException {

        Query query = SparqlQueryFactory.getPrefLabelQuery(domainId);
        Model model = getPopulatedModel();

        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            return SparqlResultParser.parsePrefLabelQueryResult(qe.execSelect());
        }
    }

    /**
     * @param domainId
     * @return non-null set of "narrower" subject ids
     * @throws java.io.IOException
     */
    public Set<String> getNarrowerDomainIds(String domainId) throws IOException {

        Query query = SparqlQueryFactory.getNarrowerDomainIdsQuery(domainId);
        Model model = getPopulatedModel();

        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            return SparqlResultParser.parseNarrowerDomainQueryResult(qe.execSelect());
        }
    }

    /**
     *
     * @param domainId the id of the domain
     * @return the non-null set of "related" domain ids if no Exception happens
     *
     * @throws IOException if failing getting input stream from taxonomy file.
     */
    public Set<String> getRelatedDomainIds(String domainId) throws IOException {

        Query query = SparqlQueryFactory.getRelatedDomainIdsQuery(domainId);
        Model model = getPopulatedModel();

        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            return SparqlResultParser.parseRelatedDomainQueryResult(qe.execSelect());
        }
    }

    @Cacheable
    private Model getPopulatedModel() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        try {
            model.read(this.skosFile.getInputStream(), "");
        } catch (IOException e) {
            LOGGER.error("Failed accessing taxonomy file!", e);
            throw e;
        }

        return model;
    }

    /**
     * The method first finds the related domains to the input domain; then find
     * the relevant courses to these domains.
     *
     * @param domainId id of the domain
     * @return get related course ids
     */
//    public Set<String> getIndirectRelatedCourseIds(String domainId) {
//	Set<String> r = new HashSet<String>();
//
//	Set<String> dIds = getRelatedDomainIds(domainId);
//
//	CourseDomainRelevanceDAO dao = new CourseDomainRelevanceDAO();
//
//	for (String dId : dIds) {
//	    r.addAll(dao.getCourseIdsByDomainId(dId));
//	}
//
//	return r;
//    }
    /**
     * @param domainId id of the domain
     * @return set of direct and indirect related courses id to the input domain
     * id
     */
//    public Set<String> getRelatedCourseIds(String domainId) {
//	Set<String> r = new HashSet<String>();
//
//	r.addAll(getIndirectRelatedCourseIds(domainId));
//	r.addAll(getDirectRelatedCourseIds(domainId));
//
//	return r;
//    }
    /**
     * The method first finds the directly related courses to this domain.
     *
     * @param domainId id of the domain
     * @return set of course ids directly related to this domain.
     */
//    public Set<String> getDirectRelatedCourseIds(String domainId) {
//	Set<String> r = new HashSet<String>();
//
//	CourseDomainRelevanceDAO dao = new CourseDomainRelevanceDAO();
//
//	r.addAll(dao.getCourseIdsByDomainId(domainId));
//
//	return r;
//    }
    /**
     * The method first finds the narrower ids to the input domain id and then
     * find related courses to these narrower ids.
     *
     * @param domainId id of the domain
     * @return set of narrower courses of this domain
     */
//    public Set<String> getNarrowerCourseIds(String domainId) {
//	Set<String> r = new HashSet<String>();
//
//	Set<String> ids = getNarrowerDomainIds(domainId);
//
//	CourseDomainRelevanceDAO dao = new CourseDomainRelevanceDAO();
//
//	for (String dId : ids) {
//	    r.addAll(dao.getCourseIdsByDomainId(dId));
//	}
//
//	return r;
//    }
    /**
     * @param domainIds
     * @return join set of related course ids to each domain id, indirectly and
     * directly
     * @see DomainReasoner#getRelatedCourseIds(String)
     */
//    public Set<String> getRelatedCourseIds(Set<String> domainIds) {
//	Set<String> r = new HashSet<String>();
//
//	for (String id : domainIds) {
//	    r.addAll(getRelatedCourseIds(id));
//	}
//
//	return r;
//    }
    /**
     * The method first get narrower domains ids and then get related course ids
     * to these domain ids
     *
     * @param domainIds
     * @return
     */
//    public Set<String> getNarrowerCourseIds(Set<String> domainIds) {
//	Set<String> r = new HashSet<String>();
//
//	for (String id : domainIds) {
//	    r.addAll(getNarrowerCourseIds(id));
//	}
//	return r;
//    }
}
