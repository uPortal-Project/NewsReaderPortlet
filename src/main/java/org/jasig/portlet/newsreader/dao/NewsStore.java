/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.dao;

import java.util.List;
import java.util.Set;

import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;


/**
 * NewsStore provides a data store for news listings and configurations.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public interface NewsStore {

	public NewsSet getNewsSet(Long id);
	
	public NewsSet getNewsSet(String userId, String setName);
	
	public List<NewsSet> getNewsSetsForUser(String userId);
	
	public void storeNewsSet(NewsSet set);
	
    /**
     * Save or update a news definition.
     *
     * @param definition NewsDefinition to be persisted
     */
    public void storeNewsDefinition(NewsDefinition definition);

    /**
     * Retrieve a news definition.
     *
     * @param id ID of the news definition to be retrieved
     * @return
     */
    public NewsDefinition getNewsDefinition(Long id);

    /**
     * Retrieve a pre-defined news definition
     *
     * @param id ID of the news definition to be retrieved
     * @return
     */
    public PredefinedNewsDefinition getPredefinedNewsDefinition(Long id);

    public PredefinedNewsDefinition getPredefinedNewsDefinitionByName(String fname);

    /**
     * Save or update a news configuration.
     *
     * @param configuration NewsConfiguration to be persisted
     */
    public void storeNewsConfiguration(NewsConfiguration configuration);

    /**
     * Retrieve a news configuration.
     *
     * @param id ID of the news configuration to be retrieved
     * @return
     */
    public NewsConfiguration getNewsConfiguration(Long id);

    /**
     * Retrieve a list of news configurations for the specified portlet.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @return
     */
    public List<NewsConfiguration> getNewsConfigurations(
            String subscribeId);

    /**
     * Retrieve a list of user-defined news configurations for
     * the specified portlet.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param visibleOnly <code>true</code> to retrieve only non-hidden news
     *                    configurations, <code>false</code> otherwise
     * @return
     */
    public List<UserDefinedNewsConfiguration> getUserDefinedNewsConfigurations(
            Long setId, boolean visibleOnly);

    /**
     * Retrieve a list of pre-defined news configurations for
     * the specified portlet.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param visibleOnly <code>true</code> to retrieve only non-hidden news
     *                    configurations, <code>false</code> otherwise
     * @return
     */
    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations(
            Long setId, boolean visibleOnly);

    /**
     * Retrieve a list of all pre-defined news configurations.
     *
     * @return
     */
    public List<PredefinedNewsConfiguration> getPredefinedNewsConfigurations();

    /**
     * Remove a news configuration from the data store
     *
     * @param configuration configuration to be removed
     */
    public void deleteNewsConfiguration(NewsConfiguration configuration);

    /**
     * Remove a news definition from the data store.
     *
     * @param definition definition to be removed
     */
    public void deleteNewsDefinition(PredefinedNewsDefinition definition);

    /**
     * Initialize news subscriptions for a given portlet subscription and role.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param roles       user roles to use to find default newss
     */
    public void initNews(NewsSet set, Set<String> roles);

    /**
     * Retrieve a list of hidden predefined newss for this portlet subscription
     * and role.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param role        user role to use to find default newss
     * @return
     */
    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(
			Long setId, Set<String> role);
	
	/**
     * Get a list of all user roles currently in use.
     *
     * @return
     */
	public List<String> getUserRoles();

}
