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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.adapter.NewsException;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.service.IInitializationService;
import org.jasig.portlet.newsreader.service.IViewResolver;
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/*
 * @author Anthony Colebourne
 */
@Controller
@RequestMapping("VIEW")
public class NewsController extends AbstractNewsController {

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
    
    @ModelAttribute
    public void getPreferences(RenderRequest request, Model model) {
    	 PortletPreferences prefs = request.getPreferences();
         model.addAttribute("storyView", prefs.getValue("summaryView", "flyout"));
         model.addAttribute("feedView", prefs.getValue("feedView", "select"));
         model.addAttribute("newWindow", Boolean.valueOf(prefs.getValue("newWindow", "true")));
    }
    
    @RenderMapping
    public String showMainView(RenderRequest request) throws Exception {

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
        
        return viewResolver.getReaderView(request);
    }

    @RenderMapping(params="action=fullStory")
    public ModelAndView fullStory(@RequestParam Long activeFeed, @RequestParam int itemIndex, RenderRequest request, RenderResponse response, Model model) throws Exception {
    	log.debug("fullStory (NewsController)");
			
    	//Security check that the feed belongs to the user
        String setName = request.getPreferences().getValue("newsSetName", "default");
    	NewsSet set = setCreationService.getNewsSet(setName, request);
    	List<NewsConfiguration> feeds = new ArrayList<NewsConfiguration>();
        feeds.addAll(set.getNewsConfigurations());
        Collections.sort(feeds);
        List<String> knownFeeds = new ArrayList<String>();
        for(NewsConfiguration feed : feeds) {
           	knownFeeds.add(String.valueOf(feed.getId()));
        }
        log.debug("Known feeds: "+knownFeeds.toString());
        if (!knownFeeds.contains(activeFeed.toString())) {
            activeFeed = null;
        	model.addAttribute("message", "Not allowed.");
    		log.debug("Not allowd.");
        }
    	
        NewsConfiguration feedConfig = newsStore.getNewsConfiguration(activeFeed); 
        log.debug("On render Active feed is " + feedConfig.getId());
        
        try {
            // get an instance of the adapter for this feed
            INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(feedConfig.getNewsDefinition().getClassName());
            // retrieve the feed from this adaptor
            NewsFeed sharedFeed = adapter.getSyndFeed(feedConfig, request);
            if (sharedFeed != null) {
               	NewsFeedItem item = sharedFeed.getEntries().get(itemIndex);
               	model.addAttribute("storyTitle", item.getTitle());
               	model.addAttribute("fullStory", item.getFullStory());
            } else {
                log.warn("Failed to get feed from adapter.");
                model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
            }
            
        } catch (NoSuchBeanDefinitionException ex) {
            log.error("News class instance could not be found: " + ex.getMessage());
            model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (NewsException ex) {
            log.warn(ex);
            model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (Exception ex) {
            log.error(ex);
            model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        }
       
        String etag = String.valueOf(model.hashCode());
        String requestEtag = request.getETag();
        
        // if the request ETag matches the hash for this response, send back
        // an empty response indicating that cached content should be used
        if (request.getETag() != null && etag.equals(requestEtag)) {
            response.getCacheControl().setExpirationTime(1);
            response.getCacheControl().setUseCachedContent(true);
            // returning null appears to cause the response to be committed
            // before returning to the portal, so just use an empty view
            return new ModelAndView("empty", Collections.<String,String>emptyMap());
        }
        
        // create new content with new validation tag
        response.getCacheControl().setETag(etag);
        response.getCacheControl().setExpirationTime(1);

        String viewName = viewResolver.getFullStoryView(request);
        return new ModelAndView(viewName, model.asMap());
    }
    
	private ApplicationContext applicationContext;
	@Autowired(required = true)
	public void setApplicationContext(ApplicationContext applicationContext)
	        throws BeansException {
	    this.applicationContext = applicationContext;
	}
	
	private NewsSetResolvingService setCreationService;
    @Autowired(required = true)
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }
}