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

import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;

public class UnsharedNewsSetServiceImpl implements NewsSetResolvingService {
	
    private static Log log = LogFactory.getLog(UnsharedNewsSetServiceImpl.class);
	
	public NewsSet getNewsSet(Long id, ActionRequest request) {
		NewsSet set = null;
		
		PortletSession session = request.getPortletSession();
		
		if (id < 0) { // No preference set, need to find a set or create a new one
			log.debug("Creating and saving new set.");
			set = createNewsSet(request);
		} else { 
			// preference already set, just fetch this news
			log.debug("Retrieving news set " + id);
			set = newsStore.getNewsSet(id);
		}

		// Persistant set is now loaded but may still need re-initalising since last use.
		// by adding setId to session, we signal that initaisation has taken place.
		
		if (session.getAttribute("setId") == null) {
			Set<String> roles = (Set<String>) session.getAttribute("userRoles", PortletSession.PORTLET_SCOPE);
			
			newsStore.initNews(set, roles);
			newsStore.storeNewsSet(set);
			session.setAttribute("setId", set.getId(), PortletSession.PORTLET_SCOPE);
		}
		
		return set;
	}
	
	private NewsSet createNewsSet(ActionRequest request) {
		NewsSet set = new NewsSet();
		set.setUserId(request.getRemoteUser());
		newsStore.storeNewsSet(set);
		return set;
	}
	
	private NewsStore newsStore;
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}
	
}
