/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.newsreader.adapter;

import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.model.PaginatingNewsFeed;

/**
 * <p>INewsAdapter interface.</p>
 *
 * @author Anthony Colebourne
 * @since 5.1.1
 */
public interface INewsAdapter {

    /**
     * Provides the fully-qualified name of the concrete INewsAdapter class,
     * which is used in creating a {@link org.jasig.portlet.newsreader.NewsDefinition}.
     *
     * @return concrete class name
     * @see org.jasig.portlet.newsreader.NewsDefinition
     */
    String getClassName();

    /**
     * Provides the key string for accessing the name of this adapter from the
     * messageBundle bean.
     *
     * @return key string for this adaptor
     */
    String getNameKey();

    /**
     * Provides the key string for accessing the description of this adapter
     * from the messageBundle bean.
     *
     * @return description for this adaptor
     */
    String getDescriptionKey();

    /**
     * <p>getSyndFeed.</p>
     *
     * @param config a {@link org.jasig.portlet.newsreader.NewsConfiguration} object
     * @param page a int
     * @param maxStories a int
     * @return a {@link org.jasig.portlet.newsreader.model.PaginatingNewsFeed} object
     * @throws org.jasig.portlet.newsreader.adapter.NewsException if any.
     */
    PaginatingNewsFeed getSyndFeed(NewsConfiguration config, int page, int maxStories) throws NewsException;

}
