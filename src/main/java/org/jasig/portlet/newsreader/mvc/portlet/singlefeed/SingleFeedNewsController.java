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

package org.jasig.portlet.newsreader.mvc.portlet.singlefeed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.Preference;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.adapter.NewsException;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.service.IInitializationService;
import org.jasig.portlet.newsreader.service.IViewResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

@Controller
@RequestMapping("VIEW")
public class SingleFeedNewsController extends AbstractNewsController {

    protected final Log log = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;
    
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
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

    @RequestMapping
    public void defaultAction(ActionRequest request) {
        // do nothing
    }
    
    @RequestMapping
    public ModelAndView showFeed(RenderRequest request, RenderResponse response) throws Exception {

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
        }

        PortletPreferences prefs = request.getPreferences();
        
        NewsConfiguration feedConfig = getFeedConfiguration(prefs);
        
        NewsFeed feed = null;
        
        try {
            // get an instance of the adapter for this feed
            INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(feedConfig.getNewsDefinition().getClassName(), INewsAdapter.class);
            // retrieve the feed from this adaptor
            feed = adapter.getSyndFeed(feedConfig, request);

            if ( feed != null )
            {
                log.debug("Got feed from adapter");
                model.put("feed", feed);
            }
            else
            {
                log.warn("Failed to get feed from adapter.");
                model.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
            }
            
        } catch (NoSuchBeanDefinitionException ex) {
            log.error("News class instance could not be found: " + ex.getMessage());
            model.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (NewsException ex) {
            log.warn(ex);
            model.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (Exception ex) {
            log.error(ex);
            model.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        }
        
        PortletPreferences portletPrefs = request.getPreferences();
        Map<String, Object> preferences = new HashMap<String, Object>();
        preferences.put(Preference.SUMMARY_VIEW_STYLE, portletPrefs.getValue(Preference.SUMMARY_VIEW_STYLE, ""));
        preferences.put(Preference.MAX_STORIES, portletPrefs.getValue(Preference.MAX_STORIES, ""));
        preferences.put(Preference.NEW_WINDOW, portletPrefs.getValue(Preference.NEW_WINDOW, Boolean.TRUE.toString()));
        preferences.put(Preference.SHOW_TITLE, portletPrefs.getValue(Preference.SHOW_TITLE, Boolean.TRUE.toString()));
        
        model.put("prefs", preferences);

        String viewName = viewResolver.getSingleFeedView(request);
        return new ModelAndView(viewName, model);
    }
    
    protected NewsConfiguration getFeedConfiguration(PortletPreferences prefs) {
        // An optional 2nd URL can be specified. This is for places where a
        // local copy of the feed is maintained by a script. The first URL
        // is the local copy and the 2nd URL is the real URL which is only
        // used if the 1st URL is unavailable for some reason, such as the
        // portal being run on a developer's box.
        String url = prefs.getValue("url", null);   // Required
        String url2 = prefs.getValue("url2", null); // Optional
        String name = prefs.getValue("name", "portlet preference 'name' not set");
        String className = prefs.getValue("className", null);

        NewsDefinition feedDef = new NewsDefinition(new Long(1), className, name);
        feedDef.addParameter("url", url);
        feedDef.addParameter("url2", url2);
        
        NewsConfiguration feedConfig = new NewsConfiguration();
        feedConfig.setNewsDefinition(feedDef);
        feedConfig.setId(new Long(1));
        return feedConfig;
    }

}
