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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

public abstract class AbstractViewResolver implements IViewResolver {

    protected static final String VIEW_NAME_PREFERENCE = "viewName";
    protected static final String MOBILE_VIEW_NAME_PREFERENCE = "mobileViewName";
    
    @Override
    public String getSingleFeedView(PortletRequest request) {
    	final PortletPreferences preferences = request.getPreferences();
        if (isMobile(request)) {
            return preferences.getValue(MOBILE_VIEW_NAME_PREFERENCE, "viewSingleFeed-jQM");
        } else {
            return preferences.getValue(VIEW_NAME_PREFERENCE, "viewSingleFeed");
        }
    }

    @Override
    public String getReaderView(PortletRequest request) {
    	final PortletPreferences preferences = request.getPreferences();
        if (isMobile(request)) {
            return preferences.getValue(MOBILE_VIEW_NAME_PREFERENCE, "viewNews-jQM");
        } else {
            return preferences.getValue(VIEW_NAME_PREFERENCE, "viewNews");
        }
    }
    
    @Override
    public String getPreferencesView(PortletRequest request) {
    	final PortletPreferences preferences = request.getPreferences();
        if (isMobile(request)) {
            return preferences.getValue(MOBILE_VIEW_NAME_PREFERENCE, "editNews-jQM");
        } else {
            return preferences.getValue(VIEW_NAME_PREFERENCE, "editNews");
        }
    }
    
    @Override
    public String getFullStoryView(PortletRequest request) {
    	final PortletPreferences preferences = request.getPreferences();
        if (isMobile(request)) {
            return preferences.getValue(MOBILE_VIEW_NAME_PREFERENCE, "fullStory-jQM");
        } else {
            return preferences.getValue(VIEW_NAME_PREFERENCE, "fullStory");
        }
    }
    
    protected abstract boolean isMobile(PortletRequest request);

}
