
package se.uu.it.cs.recsys.dataloader;

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


import se.uu.it.cs.recsys.api.exception.DataSourceFileAccessException;
import se.uu.it.cs.recsys.api.exception.DatabaseAccessException;

/**
 *
 * @author Yong Huang &lt;yong.e.huang@gmail.com&gt;
 */
public interface DataLoader {
    
    public void loadToDB() throws DatabaseAccessException, DataSourceFileAccessException;
    
}
