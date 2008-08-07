/*
Copyright (c) 2008, News Reader Portlet Development Team
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following
  disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
  disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of the News Reader Portlet Development Team nor the names of its contributors may be used to endorse or
  promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.jasig.portlet.newsreader.dao;

import org.jasig.portlet.newsreader.*;

import java.util.List;
import java.util.Set;


/**
 * NewsStore provides a data store for news listings and configurations.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public interface NewsStore {

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
            String subscribeId, boolean visibleOnly);

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
            String subscribeId, boolean visibleOnly);

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
    public void deleteNewsDefinition(NewsDefinition definition);

    /**
     * Initialize news subscriptions for a given portlet subscription and role.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param roles       user roles to use to find default newss
     */
    public void initNews(String subscribeId, Set<String> roles);

    /**
     * Retrieve a list of hidden predefined newss for this portlet subscription
     * and role.
     *
     * @param subscribeId unique ID for this portlet subscription
     * @param role        user role to use to find default newss
     * @return
     */
    public List<PredefinedNewsDefinition> getHiddenPredefinedNewsDefinitions(
			String subscribeId, Set<String> role);
	
	/**
     * Get a list of all user roles currently in use.
     *
     * @return
     */
	public List<String> getUserRoles();

}
