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
package org.jasig.portlet.newsreader.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.netbeans.lib.cvsclient.commandLine.command.log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.model.PaginatingNewsFeed;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.rometools.rome.feed.module.Module;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.MediaGroup;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

public class RomeNewsProcessorImpl {

    @Value("${newsreader.synfeedinput.xmlreader.allowDoctypes:false}")
    private boolean allowDoctypes;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private List<String> imageTypes;

    private int entriesPerPage = 10;

    public void setEntriesPerPage(int perPage) {
        this.entriesPerPage = perPage;
    }

    public void setImageTypes(List<String> imageTypes) {
        this.imageTypes = imageTypes;
    }

    private List<String> videoTypes;

    public void setVideoTypes(List<String> videoTypes) {
        this.videoTypes = videoTypes;
    }

    public PaginatingNewsFeed getFeed(InputStream in, String titlePolicy, String descriptionPolicy, int maxStories) throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
        // get a vanilla SyndFeed from the input stream
        XmlReader reader = new XmlReader(in);
        SyndFeedInput input = new SyndFeedInput();
        input.setAllowDoctypes(allowDoctypes);
        SyndFeed feed = input.build(reader);

        PaginatingNewsFeed newsFeed = new PaginatingNewsFeed(entriesPerPage);
        newsFeed.setAuthor(feed.getAuthor());
        newsFeed.setLink(feed.getLink());
        newsFeed.setTitle(feed.getTitle());
        newsFeed.setCopyright(feed.getCopyright());
        newsFeed.setMaxStories(maxStories);

        List<NewsFeedItem> newEntries = new ArrayList<>();

        // translate the default entries into our implementation
        List<SyndEntry> entries =  feed.getEntries();
        if (maxStories > 0 && maxStories < entries.size()) {
            entries = entries.subList(0, maxStories);
        }
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
        item.setCategories(entry.getCategories());
        item.setLink(entry.getLink());
        item.setUri(entry.getUri());

        if (entry.getContents() != null) {
            for (SyndContent content : (List<SyndContent>) entry.getContents()) {
                if ("html".equals(content.getType()) || "text".equals(content.getType())) {
                    item.setContent(content.getValue());
                }
            }
        }

        // AntiSamy is used to remove unwanted, or risky, HTML tags from RSS Feed data.
        // AntiSamy Policy files describe what is okay and what is not. Different portlets
        // can use different policy files.
        AntiSamy as = new AntiSamy();
        CleanResults cr = null;

        // When working with AntiSamy filter changes, it helps to know what things were
        // before AntiSamy messes them up...
        if ( log.isDebugEnabled() )
        {
            log.debug("SyndEntry Title Policy: '" + titlePolicy + "'" );
            log.debug("SyndEntry Description Policy: '" + descriptionPolicy + "'" );

            if ( entry.getTitle() != null )
            {
                log.debug("SyndEntry Pre-AntiSamy Title: '" + entry.getTitle() + "'" );
            }
            else
            {
                log.debug("SyndEntry Pre-AntiSamy Title: null value; skipping AntiSamy." );
            }

            if ( entry.getDescription() != null && entry.getDescription().getValue() != null )
            {
                log.debug("SyndEntry Pre-AntiSamy Description: '" + entry.getDescription().getValue() + "'" );
            }
            else
            {
                log.debug("SyndEntry Pre-AntiSamy Description: null value; skipping AntiSamy." );
            }
        }

        // Scrub the HTML data from the RSS Feed's Description tag...
        if (entry.getDescription() != null && entry.getDescription().getValue() != null)
        {
            // Retrieve the specified AntiSamy policy for the description...
            Policy asDescriptionPolicy = policies.get( descriptionPolicy );

            // Make sure the specified AntiSamy policy actually exists...
            if ( asDescriptionPolicy == null )
            {
                // It doesn't so, drop back to the text only policy...
                log.warn( "AntiSamy Policy NOT FOUND for Feed Description: '" + descriptionPolicy + "'." );
                log.warn( "Either the 'descriptionPolicy' portlet preference is incorrect or does not exist in the applicationContext.xml file." );
                log.warn( "Proceeding with a Text Only policy, instead." );
                asDescriptionPolicy = policies.get( "antisamy-textonly" );
            }

            // Have AntiSamy scan the description and clean out unwanted HTML tags...
            cr = as.scan(entry.getDescription().getValue(), asDescriptionPolicy);
            item.setDescription(cr.getCleanHTML());
        } else if (item.getContent() != null) {
            cr = as.scan(item.getContent(), policies.get(descriptionPolicy));
            String desc = cr.getCleanHTML();
            if (desc.length() > 200) {
                desc = desc.substring(0, 197).concat("...");
            }
            item.setDescription(desc);
        }

        if (log.isDebugEnabled() && cr != null)
        {
            log.debug("SyndEntry '" + entry.getTitle() + "' description cleaned in " + cr.getScanTime() + " seconds");
            log.debug("SyndEntry '" + entry.getTitle() + "' modified description is '" + item.getDescription() + "'" );
        }

        // Scrub the HTML data from the RSS Feed's Title tag...
        if (entry.getTitle() != null)
        {
            // Retrieve the specified AntiSamy policy for the title...
            Policy asTitlePolicy = policies.get( titlePolicy );

            // Make sure the specified AntiSamy policy actually exists...
            if ( asTitlePolicy == null )
            {
                // It doesn't so, drop back to the text only policy...
                log.warn( "AntiSamy Policy NOT FOUND for Feed Title: '" + titlePolicy + "'." );
                log.warn( "Either the 'titlePolicy' portlet preference is incorrect or does not exist in the applicationContext.xml file." );
                log.warn( "Proceeding with a Text Only policy, instead." );
                asTitlePolicy = policies.get( "antisamy-textonly" );
            }

            // Have AntiSamy scan the description and clean out unwanted HTML tags...
            cr = as.scan(entry.getTitle(), asTitlePolicy);
            item.setTitle(cr.getCleanHTML());
        }

        if (log.isDebugEnabled() && cr != null)
        {
            log.debug("SyndEntry '" + entry.getTitle() + "' title cleaned in " + cr.getScanTime() + " seconds");
            log.debug("SyndEntry '" + entry.getTitle() + "' modified title is '" + item.getTitle() + "'" );
        }

        //add more types as required

        if (entry.getPublishedDate() != null) {
            log.debug(" Entry "  + entry.getTitle() + " pub date is " + entry.getPublishedDate().toString() );
            item.setPubDate(entry.getPublishedDate());
        } else {
            log.debug("Pub date null for " + entry.getTitle() ) ;
        }

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
