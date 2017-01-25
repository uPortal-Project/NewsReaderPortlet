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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.jasig.portlet.newsreader.dao.NewsStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Spring-managed bean responsible for identifying the role(s) the current user
 * belongs to.
 *
 * @author drewwills
 */
@Service
public class RolesService {

    private static final String USER_ROLES_SESSION_ATTRIBUTE = RolesService.class.getName() + ".userRoles";

    @Autowired
    private NewsStore newsStore;

    public Set<String> getUserRoles(PortletRequest req) {

        final PortletSession session = req.getPortletSession();

        Set<String> rslt = (Set<String>) session.getAttribute(USER_ROLES_SESSION_ATTRIBUTE);
        if (rslt == null) {
            // The roles were not found in the PortletSession;  we need to initialize them
            rslt = new HashSet<>();
            final List<String> allRoles = newsStore.getUserRoles();
            for (String role : allRoles) {
                if (req.isUserInRole(role)) {
                    rslt.add(role);
                }
            }
            // Store in PortletSession for later
            session.setAttribute(USER_ROLES_SESSION_ATTRIBUTE, rslt, PortletSession.PORTLET_SCOPE);
        }

        return rslt;

    }

}
