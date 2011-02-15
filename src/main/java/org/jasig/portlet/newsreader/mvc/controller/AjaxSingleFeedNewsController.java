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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.adapter.NewsException;
import org.jasig.web.portlet.mvc.AbstractAjaxController;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class AjaxSingleFeedNewsController extends AbstractAjaxController {

	private static Log log = LogFactory.getLog(AjaxSingleFeedNewsController.class);

	@Override
	protected Map<Object, Object> handleAjaxRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		log.debug("handleAjaxRequestInternal (AjaxNewsController)");
		
		PortletPreferences prefs = request.getPreferences();
		String url = prefs.getValue("url", null);
		String name = prefs.getValue("name", "portlet preference 'name' not set");
		int maxStories = Integer.parseInt(prefs.getValue("maxStories", "10"));
        boolean showAuthor = Boolean.parseBoolean( prefs.getValue( "showAuthor", "true" ) );
		String className = prefs.getValue("className", null);
		
		JSONObject json = new JSONObject();
		
        JSONArray jsonFeeds = new JSONArray();
    	JSONObject item = new JSONObject();
    	item.put("id",1);
    	item.put("name",name);
    	jsonFeeds.add(item);
        json.put("feeds", jsonFeeds);
        json.put("activeFeed", 1);
        
        NewsDefinition feedDef = new NewsDefinition(new Long(1), className, name);
        feedDef.addParameter("url", url);
        
        NewsConfiguration feedConfig = new NewsConfiguration();
        feedConfig.setNewsDefinition(feedDef);
        feedConfig.setId(new Long(1));
		
		SyndFeed feed = null;
        ApplicationContext ctx = this.getApplicationContext();
        List<String> errors = new ArrayList<String>();
        
        try {
            // get an instance of the adapter for this feed
            INewsAdapter adapter = (INewsAdapter) ctx.getBean(className);
            // retrieve the feed from this adaptor
            feed = adapter.getSyndFeed(feedConfig, request);

            if ( feed != null )
            {
                log.debug("Got feed from adapter");

                if(feed.getEntries().isEmpty()) {
                    json.put("message", "<p>No news.</p>");
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
                    for (ListIterator i = feed.getEntries().listIterator(); i.hasNext() && i.nextIndex() < maxStories;) {
                        SyndEntry entry = (SyndEntry) i.next();
                        JSONObject jsonEntry = new JSONObject();
                        jsonEntry.put("link",entry.getLink());
                        jsonEntry.put("title",entry.getTitle());
                        jsonEntry.put("description",entry.getDescription().getValue());
                        jsonEntries.add(jsonEntry);
                    }
	            
                    jsonFeed.put("entries", jsonEntries);
	            
                    json.put("feed", jsonFeed);
                }
            }
            else
            {
                log.warn("Failed to get feed from adapter.");
                json.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
            }
            
        } catch (NoSuchBeanDefinitionException ex) {
            log.error("News class instance could not be found: " + ex.getMessage());
            json.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (NewsException ex) {
            log.warn(ex);
            json.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        } catch (Exception ex) {
            log.error(ex);
            json.put("message", "The news \"" + feedConfig.getNewsDefinition().getName() + "\" is currently unavailable.");
        }

		log.debug("forwarding to /ajaxFeedList");
		
		Map<Object, Object> model = new HashMap<Object, Object>();
		model.put("json", json);
		
		log.debug(json);
		
        return model;
	}
	
}
