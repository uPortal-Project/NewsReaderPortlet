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

package org.jasig.portlet.newsreader.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.core.io.Resource;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.mediarss.MediaEntryModule;
import com.sun.syndication.feed.module.mediarss.types.MediaContent;
import com.sun.syndication.feed.module.mediarss.types.MediaGroup;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RomeNewsProcessorImpl {
    
    protected final Log log = LogFactory.getLog(getClass());

    private List<String> imageTypes;

    public void setImageTypes(List<String> imageTypes) {
        this.imageTypes = imageTypes;
    }
    
    private List<String> videoTypes;

    public void setVideoTypes(List<String> videoTypes) {
        this.videoTypes = videoTypes;
    }

    public NewsFeed getFeed(InputStream in, String titlePolicy, String descriptionPolicy) throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
                
        // get a vanilla SyndFeed from the input stream
        XmlReader reader = new XmlReader(in);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(reader);

        NewsFeed newsFeed = new NewsFeed();
        newsFeed.setAuthor(feed.getAuthor());
        newsFeed.setLink(feed.getLink());
        newsFeed.setTitle(feed.getTitle());
        newsFeed.setCopyright(feed.getCopyright());
        
        List<NewsFeedItem> newEntries = new ArrayList<NewsFeedItem>();

        // translate the default entries into our implementation
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries =  (List<SyndEntry>) feed.getEntries();
        for (SyndEntry entry : entries) {
            NewsFeedItem item = getNewsFeedItem(entry, titlePolicy, descriptionPolicy);
            newEntries.add(item);
        }
        newsFeed.setEntries(newEntries);

        return newsFeed;
    }

    protected NewsFeedItem getNewsFeedItem(SyndEntry entry, String titlePolicy, String descriptionPolicy) throws PolicyException, ScanException {
        NewsFeedItem item = new NewsFeedItem();
        item.setAuthors(entry.getAuthors());
        item.setLink(entry.getLink());
        item.setUri(entry.getUri());
        
        if (entry.getContents() != null) {
            for (SyndContent content : (List<SyndContent>) entry.getContents()) {
                if ("html".equals(content.getType())) {
                    item.setContent(content.getValue());
                }
            }
        }

        AntiSamy as = new AntiSamy();
        CleanResults cr = null;
        double scanTime = 0;
        if (entry.getDescription() != null && entry.getDescription().getValue() != null) {
            cr = as.scan(entry.getDescription().getValue(), policies.get(descriptionPolicy));
            item.setDescription(cr.getCleanHTML());
            scanTime += cr.getScanTime();
        } else if (item.getContent() != null) {
            cr = as.scan(item.getContent(), policies.get(descriptionPolicy));
            String desc = cr.getCleanHTML();
            if (desc.length() > 200) {
                desc = desc.substring(0, 197).concat("...");
            }
            item.setDescription(desc);
            scanTime += cr.getScanTime();
        }
        
        if (entry.getTitle() != null) {
            cr = as.scan(entry.getTitle(), policies.get(titlePolicy));
            item.setTitle(cr.getCleanHTML());
            scanTime += cr.getScanTime();
        }
        if (log.isDebugEnabled() && cr != null) {
            log.debug("SyndEntry '" + entry.getTitle() + "' cleaned in " 
                                    + cr.getScanTime() + " seconds");
        }

        //add more types as required

        @SuppressWarnings("unchecked")
        List<SyndEnclosure> enclosures = entry.getEnclosures();
        for(SyndEnclosure enclosure: enclosures) {
            String type = enclosure.getType();
            if(StringUtils.isNotBlank(type) && videoTypes.contains(type)){
                item.setVideoUrl(enclosure.getUrl());
                break;
            } else if(StringUtils.isNotBlank(type) && imageTypes.contains(type)){
                item.setImageUrl(enclosure.getUrl());
                break;
            }
        }   
        
        Module mediaModule = entry.getModule(MediaEntryModule.URI);
        if (mediaModule!=null && mediaModule instanceof MediaEntryModule ){
            MediaEntryModule mentry = (MediaEntryModule) mediaModule;

            for (MediaGroup mg : mentry.getMediaGroups()) {
                for (MediaContent mc : mg.getContents()) {
                    String type = mc.getType();
                    if (StringUtils.isNotBlank(type) && videoTypes.contains(type)) {
                        item.setVideoUrl(mc.getReference().toString());
                        break;
                    } else if (StringUtils.isNotBlank(type) && imageTypes.contains(type)) {
                        item.setImageUrl(mc.getReference().toString());
                        break;
                    }
                }
                if (item.getImageUrl() == null && mg.getMetadata().getThumbnail().length != 0) {
                    item.setImageUrl(mg.getMetadata().getThumbnail()[0].getUrl().toString());
                }
            }
            
            for (MediaContent mc : mentry.getMediaContents()) {
                String type = mc.getType();
                if (StringUtils.isNotBlank(type) && videoTypes.contains(type)) {
                    item.setVideoUrl(mc.getReference().toString());
                    break;
                } else if (StringUtils.isNotBlank(type) && imageTypes.contains(type)) {
                    item.setImageUrl(mc.getReference().toString());
                    break;
                }
            }
        }
        
        return item;
    }

    private Map<String, Policy> policies = new HashMap<String, Policy>();
    
    public void setPolicies(Map<String, Resource> policies) throws PolicyException, IOException {
        for (Map.Entry<String, Resource> policy : policies.entrySet()) {
            InputStream policyStream = policy.getValue().getInputStream();
            this.policies.put(policy.getKey(), Policy.getInstance(policyStream));
            policyStream.close();
        }
    }

}
