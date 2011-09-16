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
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RomeNewsProcessorImpl {
    
    protected final Log log = LogFactory.getLog(getClass());

    private List<String> imageTypes;
    
    public SyndFeed getFeed(InputStream in, String titlePolicy, String descriptionPolicy) throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
                
        // get a vanilla SyndFeed from the input stream
        XmlReader reader = new XmlReader(in);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(reader);

        List<SyndEntry> newEntries = new ArrayList<SyndEntry>();

        // translate the default entries into our implementation
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries =  (List<SyndEntry>) feed.getEntries();
        for (SyndEntry entry : entries) {
            NewsFeedItem item = getNewsFeedItem(entry, titlePolicy, descriptionPolicy);
            newEntries.add(item);
        }
        feed.setEntries(newEntries);

        return feed;
    }

    protected NewsFeedItem getNewsFeedItem(SyndEntry entry, String titlePolicy, String descriptionPolicy) throws PolicyException, ScanException {
        NewsFeedItem item = new NewsFeedItem();
        item.copyFrom(entry);

        AntiSamy as = new AntiSamy();
        CleanResults cr = null;
        double scanTime = 0;
        if (item.getDescription() != null && item.getDescription().getValue() != null) {
            cr = as.scan(item.getDescription().getValue(), policies.get(descriptionPolicy));
            item.getDescription().setValue(cr.getCleanHTML());
            scanTime += cr.getScanTime();
        }
        if (item.getTitle() != null) {
            cr = as.scan(item.getTitle(), policies.get(titlePolicy));
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
            if(StringUtils.isNotBlank(type) && imageTypes.contains(type)){
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
                    if (StringUtils.isNotBlank(type) && imageTypes.contains(type)) {
                        item.setImageUrl(mc.getReference().toString());
                    }
                }
            }
            
            for (MediaContent mc : mentry.getMediaContents()) {
                String type = mc.getType();
                if (StringUtils.isNotBlank(type) && imageTypes.contains(type)) {
                    item.setImageUrl(mc.getReference().toString());
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

    public void setImageTypes(List<String> imageTypes) {
        this.imageTypes = imageTypes;
    }

}
