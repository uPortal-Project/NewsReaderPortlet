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

import javax.portlet.PortletRequest;

/**
 * ThemeNameViewResolver determines appropriate views by examining a "themeName"
 * portlet request property and comparing it to known mobile theme names.  This
 * implementation allows the portlet to delegate user agent inspection to the 
 * portal and also accounts for a potential user choice to use a portal version 
 * that does not match the automatic assignment.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Revision$
 */
public class ThemeNameViewResolverImpl implements IViewResolver {

    protected static final String THEME_NAME_PROPERTY = "themeName";
    protected static final String MOBILE_THEMES_KEY = "mobileThemes";
    protected static final String[] MOBILE_THEMES_DEFAULT = new String[]{ "UniversalityMobile" };
    
    public String getSingleFeedView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewSingleFeed-jQM";
        } else {
            return "viewSingleFeed";
        }
    }

    public String getReaderView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewNews-jQM";
        } else if(request.getPreferences().getValue("feedView", "tabs").equalsIgnoreCase("tabs")) {
            return "viewNews-tabs";
        } else {
            return "viewNews";
        }
    }
    
    public String getPreferencesView(PortletRequest request) {
        if (isMobile(request)) {
            return "editNews-jQM";
        } else {
            return "editNews";
        }
    }
    
    protected boolean isMobile(PortletRequest request) {
        String[] mobileThemes = request.getPreferences().getValues(MOBILE_THEMES_KEY, MOBILE_THEMES_DEFAULT);
        String themeName = request.getProperty(THEME_NAME_PROPERTY);
        if (themeName == null) {
            return false;
        }
        
        for (String theme : mobileThemes) {
            if (themeName.equals(theme)) {
                return true;
            }
        }
        
        return false;
    }
    
}
