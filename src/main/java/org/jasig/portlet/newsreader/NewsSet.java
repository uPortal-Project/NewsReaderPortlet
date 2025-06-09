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

import java.util.HashSet;
import java.util.Set;

/**
 * <p>NewsSet class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class NewsSet {

	private Long id = new Long(-1);
	private String name;
	private Set<NewsConfiguration> newsConfigurations = new HashSet<NewsConfiguration>();
	private String userId;
	
	
	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object
	 */
	public Long getId() {
		return id;
	}
	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.Long} object
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getName() {
		return name;
	}
	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * <p>Getter for the field <code>newsConfigurations</code>.</p>
	 *
	 * @return a {@link java.util.Set} object
	 */
	public Set<NewsConfiguration> getNewsConfigurations() {
		return newsConfigurations;
	}
	/**
	 * <p>Setter for the field <code>newsConfigurations</code>.</p>
	 *
	 * @param calendars a {@link java.util.Set} object
	 */
	public void setNewsConfigurations(Set<NewsConfiguration> calendars) {
		this.newsConfigurations = calendars;
	}
	/**
	 * <p>Getter for the field <code>userId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * <p>Setter for the field <code>userId</code>.</p>
	 *
	 * @param userId a {@link java.lang.String} object
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * <p>addNewsConfiguration.</p>
	 *
	 * @param config a {@link org.jasig.portlet.newsreader.NewsConfiguration} object
	 */
	public void addNewsConfiguration(NewsConfiguration config) {
		config.setNewsSet(this);
		this.newsConfigurations.add(config);
	}
	
	
}
