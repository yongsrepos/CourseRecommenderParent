/* 
 * Copyright 2015 Yong Huang <yong.e.huang@gmail.com>.
 *
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
 */
package se.uu.it.cs.recsys.constraint.builder;

/*
 * #%L
 * CourseRecommenderConstraintSolver
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jacop.core.SmallDenseDomain;
import org.jacop.set.core.BoundSetDomain;
import org.jacop.set.core.SetDomain;

/**
 * 
 */
public class DomainBuilder {
    /**
     * 
     * @param elements
     * @return an empty domain for null or empty input; otherwise, a domain with
     *         elements.
     */
    public static SetDomain createSetDomain(Set<Integer> elements) {
	List<Integer> elemts = new ArrayList<>(elements);
	Collections.sort(elemts);

	BoundSetDomain domain = new BoundSetDomain();

	if (elements == null || elements.isEmpty()) {
	    return domain;
	}

        elemts.forEach((i) -> {
            domain.addDom(i, i);
        });

	BoundSetDomain result = new BoundSetDomain(new SmallDenseDomain(),
		domain.lub());

	return result;
    }
}
