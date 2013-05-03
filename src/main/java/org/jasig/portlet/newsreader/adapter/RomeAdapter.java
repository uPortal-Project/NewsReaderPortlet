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

package org.jasig.portlet.newsreader.adapter;

import java.lang.IllegalArgumentException;

import java.io.IOException;
import java.io.InputStream;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.model.NewsFeed;
import org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

import com.sun.syndication.io.FeedException;


/**
 * RomeAdapter is a NewsAdapter for standard RSS and ATOM feeds available
 * online via http or https.
 * <p/>
 * Note: This class can process and feed supported by ROME https://rome.dev.java.net/.
 *
 * @author Anthony Colebourne
 */
public class RomeAdapter extends AbstractNewsAdapter {

    protected final Log log = LogFactory.getLog(getClass());
    
    private RomeNewsProcessorImpl processor;

    private String proxyHost = null;
    private String proxyPort = null;
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    public void setProcessor(RomeNewsProcessorImpl processor) {
        this.processor = processor;
    }

    /* (non-Javadoc)
      * @see org.jasig.portlet.newsreader.adapter.INewsAdapter#getSyndFeed(org.jasig.portlet.newsreader.NewsConfiguration, javax.portlet.PortletRequest)
      */
    public NewsFeed getSyndFeed(NewsConfiguration config, PortletRequest request) throws NewsException {

        NewsFeed feed = null;

        // Look for an alternative AntiSamy policy file in the portlet preferences. If found, use it
        // otherwise use the default policyFile being injected into this class via Spring.
        // Note that a policy file string includes a path starting at the application context.
        // (e.g. /WEB-INF/antisamy/antisamy-manchester.xml)
        PortletPreferences prefs = request.getPreferences();
        String titlePolicy = prefs.getValue( "titlePolicy", "antisamy-textonly");
        String descriptionPolicy = prefs.getValue( "descriptionPolicy", "antisamy-textonly");

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

        // try to get the feed news
        String key = getCacheKey(url);
        Element cachedElement = cache.get(key);

        if (cachedElement == null) {
            log.debug("Cache miss");

            // Do we have one URL for the feed, or two?
            if ( url2 == null )
            {
                // One URL; a normal setup. Process the URL...
                feed = getSyndFeed(url, titlePolicy, descriptionPolicy);
            }
            else
            {
                // Two URLs, so if the first fails, try the backup...
                try
                {
                    feed = getSyndFeed(url, titlePolicy, descriptionPolicy);
                }
                catch ( NewsException ex )
                {
                    log.warn( "Failed to load feed at the primary URL so trying URL2", ex );
                }

                if ( feed == null )
                {
                    // there must not be a local file cache, or it failed, so try the real url...
                    feed = getSyndFeed(url2, titlePolicy, descriptionPolicy);
                }
            }

            // save the feed to the cache
            cachedElement = new Element(key, feed);
            cache.put(cachedElement);
        } else {
            log.debug("Cache hit");
            feed = (NewsFeed) cachedElement.getValue();
        }

        // return the event list or null if the feed was not available.
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
        String proxyHost = null;
        String proxyPort = null;
        InputStream in = null;
        
        try {

            log.debug("Retrieving feed " + url);
            
            if (StringUtils.isBlank(this.proxyHost) && StringUtils.isBlank(this.proxyPort)) {
                log.trace("Checking for proxy configuration from system properties...");
                proxyHost = System.getProperty("http.proxyHost");
                proxyPort = System.getProperty("http.proxyPort");
                if (StringUtils.isBlank(proxyHost) && StringUtils.isBlank(proxyPort)) {
                    log.debug("Found proxy configuration from system properties");
                }
            }
            
            if (!StringUtils.isBlank(proxyHost) && !StringUtils.isBlank(proxyPort)) {
                client.getHostConfiguration().setProxy(proxyHost, Integer.valueOf(proxyPort));
                log.debug("Using proxy configuration to retrieve news feeds: " + proxyHost + ":" + proxyPort);
            } else {
                log.debug("No proxy configuration is set. Proceeding normally...");
            }
            
            get = new GetMethod(url);
            int rc = client.executeMethod(get);
            if (rc != HttpStatus.SC_OK) {
                log.warn("HttpStatus for " + url + ":" + rc);
            }

            // retrieve
            in = get.getResponseBodyAsStream();

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
            throw new NewsException("Error fetching feed", e);
        } catch (ScanException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed", e);
        } catch (HttpException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed", e);
        } catch (IOException e) {
            log.warn("Error fetching feed", e);
            throw new NewsException("Error fetching feed", e);
        } catch (FeedException e) {
            log.warn("Error parsing feed: ", e);
            throw new NewsException("Error parsing feed", e);
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            
            if (get != null) {
                get.releaseConnection();
            }
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

    private Cache cache;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

}
