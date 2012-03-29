package org.jasig.portlet.newsreader.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Required;

import com.sun.syndication.io.FeedException;

public class PreFetchingAdapter implements INewsAdapter {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    // always used
    private RomeNewsProcessorImpl processor;
    private Cache cache;
    private Map policies;
    
    private String titlePolicy;
    private String descriptionPolicy;
    
    private List<String> fetchedUrls; // list of the urls for the feeds to be fetched
    private boolean staticFetchedFeeds = false; // should new urls be added as peolpe subscribe, or is it just a list of common feeds set by prefs


    /* (non-Javadoc)
      * @see org.jasig.portlet.newsreader.adapter.INewsAdapter#getSyndFeed(org.jasig.portlet.newsreader.NewsConfiguration, javax.portlet.PortletRequest)
      */
    public NewsFeed getSyndFeed(NewsConfiguration config, PortletRequest request) throws NewsException {
        
        //Look for an alternative AntiSamy policy file in the portlet preferences. If found, use it
        // otherwise use the default policyFile being injected into this class via Spring.
        // Note that a policy file string includes a path starting at the application context.
        // (e.g. /WEB-INF/antisamy/antisamy-manchester.xml)
        PortletPreferences prefs = request.getPreferences();
        String titlePolicy = prefs.getValue( "titlePolicy", "antisamy-textonly");
        String descriptionPolicy = prefs.getValue( "descriptionPolicy", "antisamy-textonly");
        
        if (titlePolicy != null)
        {   this.titlePolicy = titlePolicy; }
        if (descriptionPolicy != null)
        {   this.descriptionPolicy = descriptionPolicy; }
        
        // Get the URL for this feed
        // If there is a 2nd URL, it is a fall-back in case the first does not work.
        // Using two URLs is handy if your first URL happens to be a local file cache
        // of the feed that is being maintained by an external script. This way developers
        // can still view/test the feed even if they don't have a local file cache.
        String url = config.getNewsDefinition().getParameters().get("url");
        
        if ( url == null )
        {
            throw new IllegalArgumentException( "The url parameter was not found; this is a required portlet preference." );
        }
        
        String url2 = config.getNewsDefinition().getParameters().get("url2" );
        
        // return the event list or null if the feed was not available.
        NewsFeed feed = null;
        
        
        // try to get the feed news
        String key = getCacheKey(url);
        Element cachedElement = cache.get(key);

        if (cachedElement == null) {
            log.debug("Cache miss");
            // Two URLs, so if the first fails, try the backup...
            
            if (fetchedUrls.contains(url))
            {
                log.warn("Pre-fetched feed failed to resolve cache. It could be an issue with url, or with cached timing vs frequency of fetch");
                feed = attemptGetFeed(url, url2, titlePolicy, descriptionPolicy);
            } else
            {
                if (!staticFetchedFeeds)
                {
                    // add to list of pre-fetched URLS if allowed
                    fetchedUrls.add(url);
                }
                // at this point its pretty much up to the normal cache
                feed = attemptGetFeed(url, url2, titlePolicy, descriptionPolicy);
            }
        } else {
            log.debug("Cache hit");
            
            feed = (NewsFeed) cachedElement.getValue();
        }
        return feed;
    }
    
    // seperated into its own method because its used multiple times and for ease of reading.
    private NewsFeed attemptGetFeed(String url, String url2, String titlePolicy, String descriptionPolicy)
    {
        NewsFeed feed = null;
        try
        {
            feed = getSyndFeed(url, titlePolicy, descriptionPolicy);
        }
        catch ( NewsException ex )
        {
            log.warn( "Failed to load feed at the primary URL so trying URL2", ex );
        }

        if ( feed == null ){
            // there must not be a local file cache, or it failed, so try the real url...
            feed = getSyndFeed(url2, titlePolicy, descriptionPolicy);
        }
        String key = getCacheKey(url);
        Element cachedElement = new Element(key, feed);
        cache.put(cachedElement);
        return feed;
    }
    

    /**
     * Retrieve the entire feed using HTTPClient and clean using AntiSamy,
     * build an SyndFeed object using ROME.
     *
     * @param url        String of the feed to be retrieved
     * @param policyFile String the cleaning policy
     * @return SyndFeed Feed object
     */
    protected NewsFeed getSyndFeed(String url, String titlePolicy, String descriptionPolicy) throws NewsException {
        HttpClient client = new HttpClient();
        GetMethod get = null;
        NewsFeed feed = null;

        try {

            if (log.isDebugEnabled())
                log.debug("Retrieving feed " + url);

            get = new GetMethod(url);
            int rc = client.executeMethod(get);
            if (rc != HttpStatus.SC_OK) {
                log.warn("HttpStatus for " + url + ":" + rc);
            }

            // retrieve
            InputStream in = get.getResponseBodyAsStream();

            // See if we got back any results. If so, then we can work on the results.
            // Otherwise we'd eat a parse error for trying to parse a null stream.
            if ( in != null )
            {
                feed = processor.getFeed(in, titlePolicy, descriptionPolicy);
            }
            else
            {
                log.warn( "Feed response not available or cannot be read. URL=" + url );
            }

            return feed;

        } catch (PolicyException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed");
        } catch (ScanException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed");
        } catch (HttpException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed");
        } catch (IOException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed");
        } catch (FeedException e) {
            log.warn("Error parsing feed: ", e);
            throw new NewsException("Error parsing feed");
        } finally {
            if (get != null)
                get.releaseConnection();
        }
    }
    
    public void fetchFeeds()
    {
        log.debug("Fetching " + fetchedUrls.size() + " News Feeds via quartz timer." + staticFetchedFeeds);
        for (String url: fetchedUrls)
        {
            attemptGetFeed(url, null, titlePolicy, descriptionPolicy);
        }
    }
    
    
    /**
     * Get a cache key for this feed.
     *
     * @param url URL of the feed
     * @return String representing this feed
     */
    private String getCacheKey(String url) {
        StringBuffer key = new StringBuffer();
        key.append("RomeFeed.");
        key.append(url);
        return key.toString();
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setProcessor(RomeNewsProcessorImpl processor) {
        this.processor = processor;
    }
    
    public Map getPolicies() {
        return policies;
    }

    public void setPolicies(Map policies) {
        this.policies = policies;
    }
    
    public List<String> getFetchedUrls() {
        return fetchedUrls;
    }

    @Required
    public void setFetchedUrls(List<String> fetchedUrls) {
        this.fetchedUrls = fetchedUrls;
    }
    
    public boolean isStaticFetchedFeeds() {
        return staticFetchedFeeds;
    }

    public void setStaticFetchedFeeds(boolean staticFetchedFeeds) {
        this.staticFetchedFeeds = staticFetchedFeeds;
    }
}
