
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


import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public class SparqlResultParser {
    
    static Map<String, String> parseIdAndPrefLabelQueryResult(ResultSet rs){
        if (rs == null || !rs.hasNext()) {
            return null;
        }

        final String idVarName = SKOS_SEL_VAR_NAME.DOMAIN_ID.getVarName();
        final String prefVarName = SKOS_SEL_VAR_NAME.PREF.getVarName();
        
        final Map<String, String> result = new HashMap<>();
        
        while (rs.hasNext()) {
            QuerySolution qs = rs.next();
            
            String id = qs.get(idVarName) != null ? qs.get(idVarName).toString() : "";

            if (id.isEmpty()) {
                continue;
            }
            id = id.split("#")[1];

            String pref = qs.get(prefVarName) != null ? qs.get(prefVarName).toString() : "";

            if (pref.isEmpty()) {
                continue;
            }
            pref = pref.split("@")[0];

            result.put(id, pref);
        }
        
        return result;
    }

    /**
     * 
     * @param rs
     * @return non-null instance of {@link Optional}
     */
    static Optional<String> parsePrefLabelQueryResult(ResultSet rs) {
        if (rs == null || !rs.hasNext()) {
            return Optional.empty();
        }

        final String varName = SKOS_SEL_VAR_NAME.PREF.getVarName();

        RDFNode node = rs.next().get(varName);

        if (node == null) {
            return Optional.empty();
        }

        return Optional.of(node.toString().split("@")[0]);
    }

    static Set<String> parseNarrowerDomainQueryResult(ResultSet rs) {
        if (rs == null || !rs.hasNext()) {
            return Collections.EMPTY_SET;
        }

        Set<String> r = new HashSet<>();
        final String varName = SKOS_SEL_VAR_NAME.NARROWER.getVarName();

        while (rs.hasNext()) {
            final RDFNode node = rs.next().get(varName);

            if (node == null) {
                continue;
            }

            String sID = node.toString().split("#")[1];
            r.add(sID);
        }

        return r;
    }

    static Set<String> parseRelatedDomainQueryResult(ResultSet rs) {
        if (rs == null || !rs.hasNext()) {
            return Collections.EMPTY_SET;
        }

        Set<String> r = new HashSet<>();
        final String varName = SKOS_SEL_VAR_NAME.RELATED.getVarName();

        while (rs.hasNext()) {
            final RDFNode node = rs.next().get(varName);

            if (node == null) {
                continue;
            }

            String sID = node.toString().split("#")[1];
            r.add(sID);
        }

        return r;
    }

}
