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

package org.jasig.portlet.newsreader.mvc.portlet.reader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.service.IInitializationService;
import org.jasig.portlet.newsreader.service.IViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/*
 * @author Anthony Colebourne
 */
@Controller
@RequestMapping("VIEW")
public class NewsController {

    public static final String PREFERENCE_USE_PORTAL_JS_LIBS = "usePortalJsLibs";
    public static final String PREFERENCE_PORTAL_JS_NAMESPACE = "portalJsNamespace";

    protected final Log log = LogFactory.getLog(getClass());

    private NewsStore newsStore;
    
    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }
    
    private int defaultItems = 2;
    public void setDefaultItems(int defaultItems) {
        this.defaultItems = defaultItems;
    }

    private List<IInitializationService> initializationServices;
    
    @Resource(name = "initializationServices")
    public void setInitializationServices(List<IInitializationService> services) {
        this.initializationServices = services;
    }
    
    private IViewResolver viewResolver;
    
    @Autowired(required = true)
    public void setViewResolver(IViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @ActionMapping
    public void defaultAction() {
        // do nothing
    }
    
    @RenderMapping
    public ModelAndView showMainView(RenderRequest request) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        PortletSession session = request.getPortletSession(true);

        /**
         * If this is a new session, perform any necessary
         * portlet initialization.
         */
        if (session.getAttribute("initialized") == null) {

            // get a set of all role names currently configured for
            // default newss
            List<String> allRoles = newsStore.getUserRoles();
            log.debug("all roles: " + allRoles);

            // determine which of the above roles the user belongs to
            // and store the resulting list in the session
            Set<String> userRoles = new HashSet<String>();
            for (String role : allRoles) {
                if (request.isUserInRole(role))
                    userRoles.add(role);
            }
            session.setAttribute("userRoles", userRoles, PortletSession.PORTLET_SCOPE);

            // determine if this user belongs to the defined news
            // administration group and store the result in the session
            session.setAttribute("isAdmin",
                    request.isUserInRole("newsAdmin"),
                    PortletSession.PORTLET_SCOPE);

            // set the default number of days to display
            session.setAttribute("items", defaultItems);

            // perform any other configured initialization tasks
            for (IInitializationService service : initializationServices) {
                service.initialize(request);
            }

            // mark this session as initialized
            session.setAttribute("initialized", "true");
            session.setMaxInactiveInterval(60 * 60 * 2);

        } 

        model.put("supportsHelp", request.isPortletModeAllowed(PortletMode.HELP));
        model.put("supportsEdit", request.isPortletModeAllowed(PortletMode.EDIT));
        model.put("isAdmin", session.getAttribute("isAdmin", PortletSession.PORTLET_SCOPE));
        model.put("isGuest", request.getRemoteUser() == null);
        
        PortletPreferences prefs = request.getPreferences();
        model.put(PREFERENCE_USE_PORTAL_JS_LIBS, prefs.getValue(PREFERENCE_USE_PORTAL_JS_LIBS, "true"));
        model.put(PREFERENCE_PORTAL_JS_NAMESPACE, prefs.getValue(PREFERENCE_PORTAL_JS_NAMESPACE, "up"));
        
        String viewName = viewResolver.getReaderView(request);
        return new ModelAndView(viewName, model);
    }

}