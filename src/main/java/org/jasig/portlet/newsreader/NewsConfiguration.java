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

package org.jasig.portlet.newsreader;

/**
 * NewsConfiguration represents a user-specific registration and configuration
 * for a NewsDefinition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class NewsConfiguration {

    private Long id = new Long(-1);
    private NewsDefinition newsDefinition;
    private NewsSet newsSet;
    private boolean displayed = true;
    private boolean active = true;


    /**
     * Determine whether this feed is active.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether this feed should be displayed or hidden.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Determine whether this news should be displayed or hidden.
     *
     * @return
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * Set whether this news should be displayed or hidden.
     *
     * @param displayed
     */
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    /**
     * Get the unique ID for this NewsConfiguration.
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique ID for this NewsConfiguration.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the NewsDefinition for the news being configured.
     *
     * @return
     */
    public NewsDefinition getNewsDefinition() {
        return newsDefinition;
    }

    /**
     * Set the NewsDefinition for the news being configured.
     *
     * @param definition
     */
	public void setNewsDefinition(NewsDefinition definition) {
		this.newsDefinition = definition;
	}

	public NewsSet getNewsSet() {
		return newsSet;
	}

	public void setNewsSet(NewsSet newsSet) {
		this.newsSet = newsSet;
	}
	
}
