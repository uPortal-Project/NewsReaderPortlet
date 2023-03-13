/**
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
package org.jasig.portlet.newsreader.service;

import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.portlet.util.PortletUtils;

@Service("setCreationService")
public class SharedNewsSetServiceImpl implements NewsSetResolvingService {

	private NewsStore newsStore;

	@Autowired
	private UserIdService userIdService;

	@Autowired
	private RolesService rolesService;

	private Logger logger = LoggerFactory.getLogger(SharedNewsSetServiceImpl.class);

	@Autowired
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}

	/*
	 * Get the news set from the ID or search the dataabse for a suitable set or create a new
	 * set if one cannot be found.
	 *
	 * Initalise the NewsSet
	 */
	public NewsSet getNewsSet(String fname, PortletRequest request) {

		final PortletSession session = request.getPortletSession();

		// get the user id associated with the current user, or use the configured
		// guest username if no user is authenticated
		final String userId = userIdService.getUserId(request);

		NewsSet set;

		final Object mutex = PortletUtils.getSessionMutex(session);
		synchronized (mutex) {
			logger.debug("Got Mutex {} for userId={}", mutex, userId);

			set = newsStore.getNewsSet(userId, fname);

			if (set == null) {
				logger.debug("No existing set found for "+userId+", creating and saving new set.");
		        set = new NewsSet();
		        set.setUserId(userId);
		        set.setName(fname);
		        newsStore.storeNewsSet(set);
				set = newsStore.getNewsSet(userId, fname); // get set_id
			}

			// Persistent set is now loaded but may still need re-initalising since last use.
			// by adding setId to session, we signal that initialisation has taken place.
			if (session.getAttribute("setId", PortletSession.PORTLET_SCOPE) == null) {
				logger.debug("re-initalising loaded newsSet "+set.getName());
				@SuppressWarnings("unchecked")
				final Set<String> roles = rolesService.getUserRoles(request);

				if(roles != null) { //if roles are not in session for any reason then skip initNews until later
					newsStore.initNews(set, roles);
					newsStore.storeNewsSet(set);
					session.setAttribute("setId", set.getId(), PortletSession.PORTLET_SCOPE);
				}
			}
		}
		return set;
	}

}
