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
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.adapter.NewsException;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.jasig.web.service.AjaxPortletSupportService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sun.syndication.feed.synd.SyndFeed;

@Controller
@RequestMapping("VIEW")
public class AjaxNewsController {

    protected final Log log = LogFactory.getLog(getClass());
    
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
    
    private ApplicationContext applicationContext;
    
    @Autowired(required = true)
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    private AjaxPortletSupportService ajaxPortletSupportService;
    
    @Autowired(required = true)
    public void setAjaxPortletSupportService(AjaxPortletSupportService ajaxPortletSupportService) {
            this.ajaxPortletSupportService = ajaxPortletSupportService;
    }
    
	@RequestMapping(params = "action=ajax")
	public void getJSONFeeds(ActionRequest request, ActionResponse response) throws Exception {
		log.debug("handleAjaxRequestInternal (AjaxNewsController)");
		
        Map<String, Object> model = new HashMap<String, Object>();
		
		Long setId = Long.parseLong(request.getPreferences().getValue("newsSetId", "-1"));
        NewsSet set = setCreationService.getNewsSet(setId, request);
        Set<NewsConfiguration> feeds = set.getNewsConfigurations();
        
        JSONArray jsonFeeds = new JSONArray();
        for(NewsConfiguration feed : feeds) {
        	JSONObject jsonFeed = new JSONObject();
        	jsonFeed.put("id",feed.getId());
        	jsonFeed.put("name",feed.getNewsDefinition().getName());
        	jsonFeeds.add(jsonFeed);
        }
        model.put("feeds", jsonFeeds);
       	
		PortletPreferences prefs = request.getPreferences();
		String activeateNews = request.getParameter("activeateNews");
		if (activeateNews != null) {
			prefs.setValue("activeFeed", activeateNews);
			prefs.store();
		}
		
		int maxStories = Integer.parseInt(prefs.getValue("maxStories", "10"));
		boolean showAuthor = Boolean.parseBoolean( prefs.getValue( "showAuthor", "true" ) );
		
		SyndFeed feed = null;

        // only bother to fetch the active feed
        String activeFeed = request.getPreferences().getValue("activeFeed", null);
        if (activeFeed == null && jsonFeeds.size() > 0) {
        	activeFeed = ((JSONObject) jsonFeeds.get(0)).getString("id");
			prefs.setValue("activeFeed", activeateNews);
			prefs.store();
        }
        
        if(activeFeed != null) {
	        NewsConfiguration feedConfig = newsStore.getNewsConfiguration(Long.valueOf(activeFeed));
	        model.put("activeFeed", feedConfig.getId());        
	        log.debug("On render Active feed is " + feedConfig.getId());
	        try {
	            // get an instance of the adapter for this feed
	            INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(feedConfig.getNewsDefinition().getClassName());
	            // retrieve the feed from this adaptor
	            feed = adapter.getSyndFeed(feedConfig, request);

                if ( feed != null )
                {
                    log.debug("Got feed from adapter");
	
                    if(feed.getEntries().isEmpty()) {
                        model.put("message", "<p>No news.</p>");
                    }
                    else {
                        //turn feed into JSON
                        JSONObject jsonFeed = new JSONObject();
                        
                        jsonFeed.put("link", feed.getLink());
                        jsonFeed.put("title", feed.getTitle());

                        if ( showAuthor == true )
                        {
                            jsonFeed.put("author", feed.getAuthor());
                        }

                        jsonFeed.put("copyright", feed.getCopyright());
                        
                        JSONArray jsonEntries = new JSONArray();
                        @SuppressWarnings("unchecked")
                        ListIterator<NewsFeedItem> i = (ListIterator<NewsFeedItem>) feed.getEntries().listIterator();
                        while (i.hasNext() && i.nextIndex() < maxStories) {
                            NewsFeedItem entry = i.next();
                            JSONObject jsonEntry = new JSONObject();
                            jsonEntry.put("link",entry.getLink());
                            jsonEntry.put("title",entry.getTitle());
                            jsonEntry.put("description",entry.getDescription().getValue());
                            if (entry.getImageUrl() != null) {
                                jsonEntry.put("image", entry.getImageUrl());
                            }
                            jsonEntries.add(jsonEntry);
                        }
                        
                        jsonFeed.put("entries", jsonEntries);
                        
                        model.put("feed", jsonFeed);
                    }
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
        }
        else {
        	//display message saying "Select the news you wish to read"
        	model.put("message", "Select the news you wish to read.");
        }

		log.debug("forwarding to /ajaxFeedList");
		
		this.ajaxPortletSupportService.redirectAjaxResponse("ajax/jsonView", model, request, response);
	}

}
