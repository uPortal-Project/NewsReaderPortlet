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

package org.jasig.portlet.newsreader.mvc.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.Preference;
import org.jasig.portlet.newsreader.service.IInitializationService;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.ParameterizableViewController;

public class SingleFeedNewsController extends ParameterizableViewController {

    private static Log log = LogFactory.getLog(SingleFeedNewsController.class);
    private List<IInitializationService> initializationServices = Collections.emptyList();

    public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        PortletSession session = request.getPortletSession(true);

        /**
         * If this is a new session, perform any necessary
         * portlet initialization.
         */
        if (session.getAttribute("initialized") == null) {

            // perform any other configured initialization tasks
            for (IInitializationService service : initializationServices) {
                service.initialize(request);
            }

            // mark this session as initialized
            session.setAttribute("initialized", "true");
            session.setMaxInactiveInterval(60 * 60 * 2);
        }
        
        PortletPreferences portletPrefs = request.getPreferences();
        Map<String, Object> preferences = new HashMap<String, Object>();
        preferences.put(Preference.SUMMARY_VIEW_STYLE, portletPrefs.getValue(Preference.SUMMARY_VIEW_STYLE, ""));
        preferences.put(Preference.MAX_STORIES, portletPrefs.getValue(Preference.MAX_STORIES, ""));
        preferences.put(Preference.NEW_WINDOW, portletPrefs.getValue(Preference.NEW_WINDOW, Boolean.TRUE.toString()));
        
        model.put("prefs", preferences);
        
        boolean supportsEdit = request.isPortletModeAllowed(PortletMode.EDIT);
        model.put("supportsEdit", supportsEdit);

        log.debug("forwarding to " + getViewName());
        return new ModelAndView(getViewName(), model);
    }

    public void setInitializationServices(List<IInitializationService> services) {
        this.initializationServices = services;
    }

}
