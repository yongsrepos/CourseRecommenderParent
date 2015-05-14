
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
import com.hp.hpl.jena.query.QueryFactory;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
class SparqlQueryFactory {

    enum SKOS_RELATIONSHIP {
        DOMAIN_AND_PREF_LABEL, JUST_PREF_LABEL, JUST_NARROWER, JUST_RELATED;
    }

    static Query getIdAndPrefLabelQuery() {
        final String idVarName = SKOS_SEL_VAR_NAME.DOMAIN_ID.getVarName();
        final String prefVarName = SKOS_SEL_VAR_NAME.PREF.getVarName();

        final String selStmt = "SELECT ?" + idVarName + " ?" + prefVarName + "\n "
                + "WHERE  {\n "
                + "?" + idVarName + "  a skos:Concept ; skos:prefLabel ?" + prefVarName + " .  "
                + "FILTER ( lang(?" + prefVarName + ") = \"en\" )}";
        
        final String sparql = getSparqlPrefix() + selStmt;
        
        return QueryFactory.create(sparql);
    }

    static Query getPrefLabelQuery(String domainId) {
        final String varName = SKOS_SEL_VAR_NAME.PREF.getVarName();

        final String selStmt = "SELECT ?" + varName + "\n "
                + "WHERE  {\n "
                + "<#" + domainId + "> skos:prefLabel ?" + varName + " .}";

        final String sparql = getSparqlPrefix() + selStmt;

        return QueryFactory.create(sparql);
    }

    static Query getRelatedDomainIdsQuery(String domainId) {
        final String varName = SKOS_SEL_VAR_NAME.RELATED.getVarName();

        final String selStmt = "SELECT ?" + varName + "\n "
                + "WHERE  {\n "
                + "<#" + domainId + "> skos:related ?" + varName + " . }";

        final String sparql = getSparqlPrefix() + selStmt;

        return QueryFactory.create(sparql);
    }

    static Query getNarrowerDomainIdsQuery(String domainId) {
        final String varName = SKOS_SEL_VAR_NAME.NARROWER.getVarName();

        final String selStmt = "SELECT ?" + varName + "\n "
                + "WHERE  {\n "
                + "<#" + domainId + "> skos:narrower ?" + varName + " . }";

        final String sparql = getSparqlPrefix() + selStmt;

        return QueryFactory.create(sparql);
    }

    private static String getSparqlPrefix() {
        final String PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n "
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n "
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n "
                + "PREFIX : <#>\n ";

        return PREFIX;
    }
}
