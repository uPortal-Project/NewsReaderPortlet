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
package org.jasig.portlet.newsreader.mvc.portlet.reader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.adapter.NewsException;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.model.FullStory;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.service.IInitializationService;
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

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private NewsStore newsStore;

    @Autowired
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
         model.addAttribute("maxStories", Integer.valueOf(prefs.getValue("maxStories", "-1")));
    }

    @RenderMapping
    public String showMainView(RenderRequest request) throws Exception {

        PortletSession session = request.getPortletSession(true);

        /**
         * If this is a new session, perform any necessary
         * portlet initialization.
         */
        if (session.getAttribute(INITIALIZED) == null) {

            // get a set of all role names currently configured for
            // default news
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

            // set the default number of days to display
            session.setAttribute("items", defaultItems);

            // perform any other configured initialization tasks
            for (IInitializationService service : initializationServices) {
                service.initialize(request);
            }

            // mark this session as initialized
            session.setAttribute(INITIALIZED, true);
            session.setMaxInactiveInterval(60 * 60 * 2);

        }

        /*
         * NOTE:  It's theoretically possible to override the main jsp page for
         * viewing news.  The 'videos.jsp' file, in fact, is intended to be used
         * with this feature.  In practice, there is a lot of complex JavaScript
         * in play in news.js, and it continues to evolve.  Alternate JSP views
         * atrophy and become unusable quickly.
         */
        PortletPreferences prefs = request.getPreferences();
        final String viewName = prefs.getValue("viewName", "viewNews");

        return viewName;
    }

    @RenderMapping(params="action=fullStory")
    public ModelAndView fullStory(
            @RequestParam Long activeFeed, 
            @RequestParam int itemIndex, 
            @RequestParam int page,
            RenderRequest request, 
            RenderResponse response, 
            Model model
    ) throws Exception {
        log.trace("fullStory (NewsController)");

        //Security check that the feed belongs to the user and this portlet
        String setName = request.getPreferences().getValue("newsSetName", "default");
        NewsSet set = setCreationService.getNewsSet(setName, request);
        final List<NewsConfiguration> feeds = AbstractNewsController.filterNonWhitelistedConfigurations(request, set.getNewsConfigurations());
        feeds.addAll(set.getNewsConfigurations());
        Collections.sort(feeds);
        JSONArray jsonFeeds = new JSONArray();
        List<String> knownFeeds = new ArrayList<String>();
        for(NewsConfiguration feed : feeds) {
            if (feed.isDisplayed()) {
                JSONObject jsonFeed = new JSONObject();
                jsonFeed.put("id", feed.getId());
                jsonFeed.put("name", feed.getNewsDefinition().getName());
                jsonFeeds.add(jsonFeed);
                knownFeeds.add(String.valueOf(feed.getId()));
            }
        }
        log.debug("Known feeds: "+knownFeeds.toString());
        model.addAttribute("feeds", jsonFeeds);
        if (!knownFeeds.contains(activeFeed.toString())) {
            activeFeed = null;
            model.addAttribute("message", "Not allowed.");
            log.debug("Not allowd.");
        }
        model.addAttribute("activeFeed", activeFeed);
        
        NewsConfiguration feedConfig = newsStore.getNewsConfiguration(activeFeed); 
        log.debug("On render Active feed is " + feedConfig.getId());
        
        try {
            // get an instance of the adapter for this feed
            INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(feedConfig.getNewsDefinition().getClassName());
            // Get max stories (needed to match cache check)
            int maxStories = getMaxStories(request.getPreferences());
            // retrieve the feed from this adaptor
            NewsFeed sharedFeed = adapter.getSyndFeed(feedConfig, page, maxStories);
            if (sharedFeed != null) {
               NewsFeedItem item = sharedFeed.getEntries().get(itemIndex);
               model.addAttribute("storyTitle", item.getTitle());

               FullStory fullStory = item.getFullStory();                  
               model.addAttribute("fullStory", fullStory.getFullStoryText());
            } else {
                log.warn("Failed to get feed from adapter.");
                model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
            }

            PortletPreferences prefs = request.getPreferences();
            model.addAttribute("feedView", prefs.getValue("feedView", "select"));

        } catch (NoSuchBeanDefinitionException ex) {
            log.error("News class instance could not be found: " + ex.getMessage());
            model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (NewsException ex) {
            log.warn(ex.getMessage(), ex);
            model.addAttribute("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
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

        return new ModelAndView("fullStory", model.asMap());
    }

    private ApplicationContext applicationContext;
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    private NewsSetResolvingService setCreationService;
    @Autowired
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }
}
