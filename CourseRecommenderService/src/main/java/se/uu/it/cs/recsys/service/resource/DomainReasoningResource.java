package se.uu.it.cs.recsys.service.resource;

/*
 * #%L
 * CourseRecommenderService
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
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.type.ComputingDomain;
import se.uu.it.cs.recsys.persistence.repository.ComputingDomainRepository;
import se.uu.it.cs.recsys.semantic.ComputingDomainReasoner;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com>&gt;
 */
@Component
@Path("/domains")
@Api(value = "domains", description = "Reasoning on computing domains.")
public class DomainReasoningResource {

    @Autowired
    private ComputingDomainRepository computingDomainRepository;

    @Autowired
    private ComputingDomainReasoner domainReasoner;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "all", response = ComputingDomain.class, responseContainer = "Set")
    public Response getAllComputingDomains() {

        List<se.uu.it.cs.recsys.persistence.entity.ComputingDomain> all
                = this.computingDomainRepository.findAll();

        Set<ComputingDomain> output = all.stream()
                .map(entity -> new ComputingDomain(entity.getId(), entity.getName()))
                .collect(Collectors.toSet());

        return Response.ok(output, MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("/narrower")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "narrower", response = ComputingDomain.class, responseContainer = "Set")
    public Response getNarrowerComputingDomains(@QueryParam("domainIds") Set<String> domainIds) {

        Set<ComputingDomain> output = new HashSet<>();

        Set<String> narrowerIds = this.domainReasoner.getNarrowerCourseIdsForCollection(domainIds);

        narrowerIds.stream().forEach(
                domainId -> {
                    se.uu.it.cs.recsys.persistence.entity.ComputingDomain entity
                    = this.computingDomainRepository.findById(domainId);

                    output.add(new ComputingDomain(entity.getId(), entity.getName()));
                }
        );

        return Response.ok(output, MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("/related")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "related", response = ComputingDomain.class, responseContainer = "Set")
    public Response getRelatedComputingDomains(@QueryParam("domainIds") Set<String> domainIds) {

        Set<ComputingDomain> output = new HashSet<>();

        Set<String> relatedIds = this.domainReasoner.getRelatedCourseIdsForCollection(domainIds);

        relatedIds.stream().forEach(
                domainId -> {
                    se.uu.it.cs.recsys.persistence.entity.ComputingDomain entity
                    = this.computingDomainRepository.findById(domainId);

                    output.add(new ComputingDomain(entity.getId(), entity.getName()));
                }
        );

        return Response.ok(output, MediaType.APPLICATION_JSON).build();

    }

}
