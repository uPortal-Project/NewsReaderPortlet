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

import java.util.*;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import net.sf.json.JSONArray;
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
import org.jasig.portlet.newsreader.service.NewsSetResolvingService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping("VIEW")
public class AjaxNewsController {

    protected final Log log = LogFactory.getLog(getClass());

    private NewsStore newsStore;

    @Autowired
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    private NewsSetResolvingService setCreationService;

    @Autowired
    public void setSetCreationService(NewsSetResolvingService setCreationService) {
        this.setCreationService = setCreationService;
    }

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    @ResourceMapping
    public ModelAndView getJSONFeeds(ResourceRequest request, ResourceResponse response) throws Exception {
        log.debug("handleAjaxRequestInternal (AjaxNewsController)");

        Map<String, Object> model = new HashMap<>();

        String feedView = request.getPreferences().getValue("feedView", null);
        log.debug("Feed view type is " + feedView);
        boolean getAllFeedItems = "all".equals(feedView);
        log.debug("Get all feed items is " + getAllFeedItems);

        String setName = request.getPreferences().getValue("newsSetName", "default");
        NewsSet set = setCreationService.getNewsSet(setName, request);
        final List<NewsConfiguration> feeds = AbstractNewsController.filterNonWhitelistedConfigurations(request, set.getNewsConfigurations());
        Collections.sort(feeds);

        log.debug("Number of feeds found: " + feeds.size());

        JSONArray jsonFeeds = new JSONArray();
        List<String> knownFeeds = new ArrayList<String>();
        for (NewsConfiguration feed : feeds) {

           log.debug("Processing feed: " + feed.getNewsDefinition().getName()) ;

           /*
            * Don't display the "All" feed - that feed is just present
            * in news administration so we can give the user the option
            * to show all news enteries from all feeds in the view
            * This was a KU added feature to the news reader portlet
            */
           if (!getAllFeedItems && feed.getNewsDefinition().getName().equals("All")) {
               feed.setDisplayed(false);
           }

           if (feed.isDisplayed()) {
                JSONObject jsonFeed = new JSONObject();
                jsonFeed.put("id", feed.getId());
                jsonFeed.put("name", feed.getNewsDefinition().getName());
                jsonFeeds.add(jsonFeed);
                knownFeeds.add(String.valueOf(feed.getId()));
               log.debug("Value of jsonFeed: " + jsonFeed);
            }
        }
        model.put("feeds", jsonFeeds);

        PortletPreferences prefs = request.getPreferences();
        String activeateNews = request.getParameter("activeateNews");
        if (activeateNews != null) {
            prefs.setValue("activeFeed", activeateNews);
            prefs.store();
        }

        int page = Integer.parseInt(request.getParameter("page"));

        /*
         * If the user selected to view all feeds combined
         * then set the active feed to be the fake all feed
         * This is so we can give the user the option
         * to show all news entries from all feeds in the view
         * This was a KU added feature to the news reader portlet
         */
        if (getAllFeedItems) {
            for (NewsConfiguration newsConfig : feeds) {
                if (newsConfig.getNewsDefinition().getName().equals("All")) {
                    prefs.setValue("activeFeed", newsConfig.getId().toString());
                    prefs.store();
                    break;
                }
            }
        }

        // only bother to fetch the active feed
        String activeFeed = request.getPreferences().getValue("activeFeed", null);

        // if the current active feed no longer exists in the news set, unset it
        if (!knownFeeds.contains(activeFeed)) {
            activeFeed = null;
        }

        // if no active feed is currently set, use the first feed in the list
        if (activeFeed == null && jsonFeeds.size() > 0) {
            activeFeed = ((JSONObject) jsonFeeds.get(0)).getString("id");
            prefs.setValue("activeFeed", activeateNews);
            prefs.store();
        }

        if (activeFeed != null) {
            NewsConfiguration feedConfig = newsStore.getNewsConfiguration(Long.valueOf(activeFeed));
            model.put("activeFeed", feedConfig.getId());
            log.debug("On render Active feed is " + feedConfig.getId());
            
            model.put("page", page);
            
            try {
                NewsFeed sharedFeed;

                /*
                 * If user selected all feeds combined in edit news
                 * then we need to process each news feed and put all
                 * the news feed entries into the model object
                 * that will be rendered in the view.  This is a KU
                 * added feature to the community version.
                 */
                if (getAllFeedItems) {

                    log.debug("Getting all feed items");

                    List<NewsFeedItem> allFeedItems = new ArrayList<>();

                    for (NewsConfiguration newsConfig : feeds) {

                        log.debug("Getting feed items for " + newsConfig.getNewsDefinition().getName());

                        if (newsConfig.getNewsDefinition().getName().equals("All") || !newsConfig.isDisplayed()) {
                            log.debug("Breaking out of for loop and not getting feed entries");
                            continue;
                        }
                        INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(newsConfig.getNewsDefinition().getClassName());

                        feedConfig = newsStore.getNewsConfiguration( newsConfig.getId());

                        NewsFeed feed = adapter.getSyndFeed(feedConfig, page);
                        List<NewsFeedItem> feedItems = feed.getEntries();

                        log.debug("Number of feed entries for " + newsConfig.getNewsDefinition().getName() + " is " + feedItems.size() );

                        for (int i = 0; i < feedItems.size(); i++) {
                            allFeedItems.add(feedItems.get(i));
                        }
                    }

                    sharedFeed = new NewsFeed();

                    Collections.sort(allFeedItems);
                    sharedFeed.setTitle("News Feed");
                    sharedFeed.setAuthor("Various");
                    Date year = new Date();
                    sharedFeed.setCopyright(Integer.toString(year.getYear()));
                    sharedFeed.setEntries(allFeedItems);

                    log.debug("TOTAL Number of feed items: " + sharedFeed.getEntries().size() );

                } else {

                    // get an instance of the adapter for this feed
                    INewsAdapter adapter = (INewsAdapter) applicationContext.getBean(feedConfig.getNewsDefinition().getClassName());
                    // retrieve the feed from this adaptor
                    sharedFeed = adapter.getSyndFeed(feedConfig, page);
                }
                if (sharedFeed != null) {
                    List<NewsFeedItem> items = sharedFeed.getEntries();
                    for (int i = 0; i < items.size(); i++) {
                        NewsFeedItem item = items.get(i);
                        if (item.getLink() == null && item.getFullStory() != null) {
                            PortletURL link = response.createRenderURL();
                            link.setParameter("action", "fullStory");
                            link.setParameter("activeFeed", feedConfig.getId().toString());
                            link.setParameter("itemIndex", String.valueOf(i));
                            link.setParameter("page", Integer.toString(page));
                            item.setLink(link.toString());
                        }
                    }

                    model.put("feed", sharedFeed);
                } else {
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
        } else {
            //display message saying "Select the news you wish to read"
            model.put("message", "Select the news you wish to read.");
        }

        log.debug("forwarding to /ajaxFeedList");

        String etag = String.valueOf(model.hashCode());
        String requestEtag = request.getETag();

        // if the request ETag matches the hash for this response, send back
        // an empty response indicating that cached content should be used
        if (request.getETag() != null && etag.equals(requestEtag)) {
            response.getCacheControl().setExpirationTime(1);
            response.getCacheControl().setUseCachedContent(true);
            // returning null appears to cause the response to be committed
            // before returning to the portal, so just use an empty view
            return new ModelAndView("empty", Collections.<String, String>emptyMap());
        }

        // create new content with new validation tag
        response.getCacheControl().setETag(etag);
        response.getCacheControl().setExpirationTime(1);

        return new ModelAndView("json", model);
    }

}
