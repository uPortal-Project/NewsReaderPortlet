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

package org.jasig.portlet.newsreader.service;

import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;

public class SharedNewsSetServiceImpl implements NewsSetResolvingService {

	private static Log log = LogFactory.getLog(SharedNewsSetServiceImpl.class);

	/*
	 * Get the news set from the ID or search the dataabse for a suitable set or create a new
	 * set if one cannot be found.
	 * 
	 * Initalise the NewsSet  
	 */
	public NewsSet getNewsSet(String fname, PortletRequest request) {

		PortletSession session = request.getPortletSession();

		// get the user id associated with the current user, or use the configured
		// guest username if no user is authenticated
		String userId = request.getRemoteUser();
		if (userId == null) {
		    userId = "guest";
		}
		
		NewsSet set = newsStore.getNewsSet(userId, fname);
		if (set == null) {
			log.debug("No existing set found, creating and saving new set.");
	        set = new NewsSet();
	        set.setUserId(userId);
	        set.setName(fname);
	        newsStore.storeNewsSet(set);
		}
		
		// Persistent set is now loaded but may still need re-initalising since last use.
		// by adding setId to session, we signal that initialisation has taken place.
        if (session.getAttribute("setId") == null) {
            @SuppressWarnings("unchecked")
            Set<String> roles = (Set<String>) session.getAttribute("userRoles", PortletSession.PORTLET_SCOPE);
			
			newsStore.initNews(set, roles);
			newsStore.storeNewsSet(set);
			session.setAttribute("setId", set.getId(), PortletSession.PORTLET_SCOPE);
		}
		
		return set;
	}

	private NewsStore newsStore;
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}

}
