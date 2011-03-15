package org.jasig.portlet.newsreader.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
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

public class RomeNewsProcessorImpl implements InitializingBean {
    
    protected final Log log = LogFactory.getLog(getClass());

    private List<String> imageTypes;
    
    public SyndFeed getFeed(InputStream in) throws IOException, IllegalArgumentException, FeedException, PolicyException, ScanException {
                
        // get a vanilla SyndFeed from the input stream
        XmlReader reader = new XmlReader(in);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(reader);

        List<SyndEntry> newEntries = new ArrayList<SyndEntry>();

        // translate the default entries into our implementation
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries =  (List<SyndEntry>) feed.getEntries();
        for (SyndEntry entry : entries) {
            NewsFeedItem item = getNewsFeedItem(entry);
            newEntries.add(item);
        }
        feed.setEntries(newEntries);

        return feed;
    }

    protected NewsFeedItem getNewsFeedItem(SyndEntry entry) throws PolicyException, ScanException {
        NewsFeedItem item = new NewsFeedItem();
        item.copyFrom(entry);

        AntiSamy as = new AntiSamy();
        CleanResults cr = null;
        double scanTime = 0;
        if (item.getDescription() != null && item.getDescription().getValue() != null) {
            cr = as.scan(item.getDescription().getValue(), descriptionPolicy);
            item.getDescription().setValue(cr.getCleanHTML());
            scanTime += cr.getScanTime();
        }
        if (item.getTitle() != null) {
            cr = as.scan(item.getTitle(), titlePolicy);
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

    private Resource titleResource;
    
    @javax.annotation.Resource(name = "titlePolicyFile")
    @Required
    public void setTitleResource(Resource titleResource) {
        this.titleResource = titleResource;
    }

    private Resource descriptionResource;
    
    @javax.annotation.Resource(name = "descriptionPolicyFile")
    @Required
    public void setDescriptionResource(Resource descriptionResource) {
        this.descriptionResource = descriptionResource;
    }

    private Policy titlePolicy;
    
    private Policy descriptionPolicy;

    public void afterPropertiesSet() throws Exception {
        // create an AntiSamy policy object from the configured policy file
        InputStream titleStream = titleResource.getInputStream();
        this.titlePolicy = Policy.getInstance(titleStream);
        titleStream.close();
        
        InputStream descriptionStream = descriptionResource.getInputStream();
        this.descriptionPolicy = Policy.getInstance(descriptionStream);
        descriptionStream.close();
    }
    
    
    public void setImageTypes(List<String> imageTypes) {
        this.imageTypes = imageTypes;
    }

}
