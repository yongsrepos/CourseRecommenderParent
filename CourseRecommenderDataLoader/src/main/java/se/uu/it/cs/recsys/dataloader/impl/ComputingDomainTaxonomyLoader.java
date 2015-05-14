
package se.uu.it.cs.recsys.dataloader.impl;

/*
 * #%L
 * CourseRecommenderDataLoader
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.api.exception.DatabaseAccessException;
import se.uu.it.cs.recsys.dataloader.DataLoader;
import se.uu.it.cs.recsys.persistence.entity.ComputingDomain;
import se.uu.it.cs.recsys.persistence.repository.ComputingDomainRepository;
import se.uu.it.cs.recsys.semantic.ComputingDomainReasoner;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
@Component
public class ComputingDomainTaxonomyLoader implements DataLoader {

    @Autowired
    private ComputingDomainReasoner computingDomainReasoner;

    @Autowired
    private ComputingDomainRepository computingDomainRepository;

    @Override
    public void loadToDB() throws DataSourceFileAccessException, DatabaseAccessException {
        Map<String, String> domainIdAndName;

        try {
            domainIdAndName = this.computingDomainReasoner.getIdAndLabel();
        } catch (IOException ex) {
            final String MSG = "Failed retrieving computing domain info: ";
            throw new DataSourceFileAccessException(MSG, ex);

        }

        List<ComputingDomain> domainList = domainIdAndName.entrySet().stream().
                map(entry -> {
                    ComputingDomain entity = new ComputingDomain(entry.getKey());
                    entity.setName(entry.getValue());
                    return entity;
                })
                .collect(Collectors.toList());

        this.computingDomainRepository.save(domainList);
    }
}
