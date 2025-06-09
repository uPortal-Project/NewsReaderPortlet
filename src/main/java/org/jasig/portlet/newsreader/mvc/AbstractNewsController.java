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
package org.jasig.portlet.newsreader.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.service.NewsConfigurationWhitelist;
import org.jasig.portlet.newsreader.service.Whitelist;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Creating an abstract superclass for news in VIEW mode as a first step toward
 * combining controllers for single-feed and regular.  I believe we'd be better
 * served -- have less chance for mismatch and regression -- if the 2 approaches
 * used the same controllers, and the differences in behavior were factored out
 * into different subclasses of service objects.  We could then configure these
 * services appropriately in each portlet's Spring sub-context.
 *
 * So I suggest factoring common code out of the subclasses (and into here)
 * bit-by-bit until the controllers are no different, at which point we can
 * remove this class if it seems like a good idea then.
 *
 * @author awills
 * @since 5.1.1
 */
public class AbstractNewsController {
    /** Constant <code>INITIALIZED="initialized"</code> */
    public static final String INITIALIZED = "initialized";
    /** Constant <code>NEWS_ADMIN_ROLE="newsAdmin"</code> */
    public static final String NEWS_ADMIN_ROLE = "newsAdmin";

    /** Constant <code>ALLOW_EDIT_PREFERENCE="allowEdit"</code> */
    public static final String ALLOW_EDIT_PREFERENCE = "allowEdit";
    /** Constant <code>ALLOW_HELP_PREFERENCE="allowHelp"</code> */
    public static final String ALLOW_HELP_PREFERENCE = "allowHelp";

    private static final NewsConfigurationWhitelist WHITELIST = new NewsConfigurationWhitelist();

    /**
     * Utility function for filtering a collection of NewsConfiguration objects
     * based on the Whitelist for this portlet-definition.
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @param items a {@link java.util.Collection} object
     * @return a {@link java.util.List} object
     */
    public static List<NewsConfiguration> filterNonWhitelistedConfigurations(PortletRequest req, Collection<NewsConfiguration> items) {
    	List<NewsConfiguration> filtered = WHITELIST.filter(req, items);
    	return filtered;
    }

    /**
     * <p>isAdmin.</p>
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @return a boolean
     */
    @ModelAttribute("isAdmin")
    public boolean isAdmin(PortletRequest req) {
        return req.isUserInRole(NEWS_ADMIN_ROLE);
    }

    /**
     * <p>isGuest.</p>
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @return a boolean
     */
    @ModelAttribute("isGuest")
    public boolean isGuest(PortletRequest req) {
        return req.getRemoteUser() == null;
    }

    /**
     * <p>supportsEdit.</p>
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @return a boolean
     */
    @ModelAttribute("supportsEdit")
    public boolean supportsEdit(PortletRequest req) {
        final PortletPreferences prefs = req.getPreferences();
        final String allowEdit = prefs.getValue(ALLOW_EDIT_PREFERENCE, "true");
        return Boolean.parseBoolean(allowEdit);
    }

    /**
     * <p>supportsHelp.</p>
     *
     * @param req a {@link javax.portlet.PortletRequest} object
     * @return a boolean
     */
    @ModelAttribute("supportsHelp")
    public boolean supportsHelp(PortletRequest req) {
        // workaround for UP-3267
        //return req.isPortletModeAllowed(PortletMode.HELP);
    	
    	final PortletPreferences prefs = req.getPreferences();
        final String allowHelp = prefs.getValue(ALLOW_HELP_PREFERENCE, "true");
        return Boolean.parseBoolean(allowHelp);
    }

    /**
     * <p>getMaxStories.</p>
     *
     * @param prefs a {@link javax.portlet.PortletPreferences} object
     * @return a int
     */
    public static int getMaxStories(final PortletPreferences prefs) {
        int maxStories = -1;
        final String maxPref = prefs.getValue("maxStories", "-1");
        try {
            maxStories = Integer.parseInt(maxPref);
        } catch (NumberFormatException nfe) {
            // fall back to -1
        }
        return maxStories;
    }

}
