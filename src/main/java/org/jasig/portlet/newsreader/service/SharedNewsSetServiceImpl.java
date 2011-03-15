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

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;

public class SharedNewsSetServiceImpl implements NewsSetResolvingService {

	private static Log log = LogFactory.getLog(SharedNewsSetServiceImpl.class);

	/*
	 * Create and store a new news set 
	 */
	private NewsSet createNewsSet(ActionRequest request) {
		NewsSet set = new NewsSet();
		set.setUserId(request.getRemoteUser());
		newsStore.storeNewsSet(set);
		return set;
	}

	/*
	 * Get the news set from the ID or search the dataabse for a suitable set or create a new
	 * set if one cannot be found.
	 * 
	 * Initalise the NewsSet  
	 */
	public NewsSet getNewsSet(Long id, ActionRequest request) {
		NewsSet set = null;

		PortletSession session = request.getPortletSession();
		
		
		if (id < 0) { // No preference set, need to find a set or create a new one
			List<NewsSet> sets = newsStore.getNewsSetsForUser(request.getRemoteUser());
			if (!sets.isEmpty()) {
				set = sets.get(0);
				log.debug("Found existing set ("+set.getId()+") for user, returning this for shared use.");
			}
			else {
				log.debug("No existing set found, creating and saving new set.");
				set = createNewsSet(request);
			}
			
			// now we have a set, assocoiate it with this portlet instance.
			PortletPreferences preferences = request.getPreferences();
			try {
				preferences.setValue("newsSetId", String.valueOf(set.getId()));
				preferences.store();
			} catch (ReadOnlyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else { // preference already set, just fetch this news
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

	private NewsStore newsStore;
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}

}
