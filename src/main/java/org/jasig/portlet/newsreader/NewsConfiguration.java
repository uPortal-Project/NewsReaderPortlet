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
package org.jasig.portlet.newsreader;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * NewsConfiguration represents a user-specific registration and configuration
 * for a NewsDefinition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
public class NewsConfiguration implements Comparable<NewsConfiguration> {

    private Long id = new Long(-1);
    private NewsDefinition newsDefinition;
    private NewsSet newsSet;
    private boolean displayed = true;
    private boolean active = true;


    /**
     * Determine whether this feed is active.
     *
     * @return a boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether this feed should be displayed or hidden.
     *
     * @param active a boolean
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Determine whether this news should be displayed or hidden.
     *
     * @return a boolean
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * Set whether this news should be displayed or hidden.
     *
     * @param displayed a boolean
     */
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    /**
     * Get the unique ID for this NewsConfiguration.
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique ID for this NewsConfiguration.
     *
     * @param id a {@link java.lang.Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the NewsDefinition for the news being configured.
     *
     * @return a {@link org.jasig.portlet.newsreader.NewsDefinition} object
     */
    public NewsDefinition getNewsDefinition() {
        return newsDefinition;
    }

	/**
	 * Set the NewsDefinition for the news being configured.
	 *
	 * @param definition a {@link org.jasig.portlet.newsreader.NewsDefinition} object
	 */
	public void setNewsDefinition(NewsDefinition definition) {
		this.newsDefinition = definition;
	}

	/**
	 * <p>Getter for the field <code>newsSet</code>.</p>
	 *
	 * @return a {@link org.jasig.portlet.newsreader.NewsSet} object
	 */
	public NewsSet getNewsSet() {
		return newsSet;
	}

	/**
	 * <p>Setter for the field <code>newsSet</code>.</p>
	 *
	 * @param newsSet a {@link org.jasig.portlet.newsreader.NewsSet} object
	 */
	public void setNewsSet(NewsSet newsSet) {
		this.newsSet = newsSet;
	}
	
    /** {@inheritDoc} */
    @Override
    public int compareTo(NewsConfiguration config) {
        return new CompareToBuilder()
            .append(this.getNewsDefinition().getName(), config.getNewsDefinition().getName())
            .append(this.id, config.getId())
            .toComparison();
    }
	    
}
