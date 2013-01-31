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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.service.IViewResolver;
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;


/**
 * EditNewsPreferencesController provides the main edit page for the News Reader
 * portlet.  The page allows users to view, add, delete and edit all available
 * feeds.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Controller
@RequestMapping("EDIT")
public class EditNewsPreferencesController {

    protected final Log log = LogFactory.getLog(getClass());

    private Map<String,String> predefinedEditActions;

    @Resource(name = "predefinedEditActions")
    public void setPredefinedEditActions(Map<String,String> predefinedEditActions) {
        this.predefinedEditActions = predefinedEditActions;
    }

    private NewsStore newsStore;

    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    private NewsSetResolvingService setCreationService;
    
    @Autowired(required = true)
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }
    
    private IViewResolver viewResolver;
    
    @Autowired(required = true)
    public void setViewResolver(IViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @RenderMapping
    public ModelAndView showPreferencesView(RenderRequest request,
            RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();

        PortletSession session = request.getPortletSession();
        String setName = request.getPreferences().getValue("newsSetName", "default");
        NewsSet set = setCreationService.getNewsSet(setName, request);
        Set<NewsConfiguration> configurations = set.getNewsConfigurations();
        
        // divide the configurations into user-defined and pre-defined
        // configurations for display
        List<UserDefinedNewsConfiguration> myNewsConfigurations = new ArrayList<UserDefinedNewsConfiguration>();
        List<PredefinedNewsConfiguration> predefinedNewsConfigurations = new ArrayList<PredefinedNewsConfiguration>();
        for (NewsConfiguration configuration : configurations) {
        	if (configuration instanceof UserDefinedNewsConfiguration) {
        		myNewsConfigurations.add((UserDefinedNewsConfiguration) configuration);
        	} else if (configuration instanceof PredefinedNewsConfiguration) {
        		predefinedNewsConfigurations.add((PredefinedNewsConfiguration) configuration);
        	}
        }
        Collections.sort(myNewsConfigurations);
        Collections.sort(predefinedNewsConfigurations);
        
        model.put("myNewsConfigurations", myNewsConfigurations);
        model.put("predefinedNewsConfigurations", predefinedNewsConfigurations);

        // get the user's role listings
        @SuppressWarnings("unchecked")
        Set<String> userRoles = (Set<String>) session.getAttribute("userRoles", PortletSession.PORTLET_SCOPE);

        // get a list of predefined feeds the user doesn't
        // currently have configured
        List<PredefinedNewsDefinition> definitions = newsStore.getHiddenPredefinedNewsDefinitions(set.getId(), userRoles);
        model.put("hiddenFeeds", definitions);

        model.put("predefinedEditActions", predefinedEditActions);

        // return the edit view
        String viewName = viewResolver.getPreferencesView(request);
        return new ModelAndView(viewName, "model", model);
    }

    @ActionMapping
    protected void saveNewsPreference(ActionRequest request,
            ActionResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        String actionCode = request.getParameter("actionCode");
        PortletSession session = request.getPortletSession();
        Long setId = (Long) session.getAttribute("setId", PortletSession.PORTLET_SCOPE);
        NewsSet set = newsStore.getNewsSet(setId);

        if (actionCode.equals("delete")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            newsStore.deleteNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("show")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(true);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("hide")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(false);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("showNew")) {
            // get user information
            PredefinedNewsDefinition definition = (PredefinedNewsDefinition) newsStore.getNewsDefinition(id);
            log.debug("definition to save " + definition.toString());
            PredefinedNewsConfiguration config = new PredefinedNewsConfiguration();
            config.setNewsDefinition(definition);
            config.setNewsSet(set);
            newsStore.storeNewsConfiguration(config);
        }
    }

    @ResourceMapping
    public ModelAndView saveDisplayPreference(ResourceRequest request,
            ResourceResponse response) throws IOException {

        Map<String, ?> model;
        
        try {
            String prefName = request.getParameter("prefName");
            String prefValue = request.getParameter("prefValue");
            
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(prefName, prefValue);
            prefs.store();

            model = Collections.singletonMap("status", "success");

        } catch (Exception e) {
            log.error("There was an error saving the preferences.", e);
            model = Collections.singletonMap("status", "failure");
        }

        return new ModelAndView("json", model);

    }

}
