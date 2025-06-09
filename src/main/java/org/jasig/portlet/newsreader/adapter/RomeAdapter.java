/*
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
package org.jasig.portlet.newsreader.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.model.PaginatingNewsFeed;
import org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;
import com.rometools.rome.io.FeedException;;

/**
 * RomeAdapter is a NewsAdapter for standard RSS and ATOM feeds available
 * online via http or https.
 * <p>
 * Note: This class can process and feed supported by ROME https://rome.dev.java.net/.
 *
 * @author Anthony Colebourne
 * @since 5.1.1
 */
public class RomeAdapter extends AbstractNewsAdapter {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /** Constant <code>PREFERENCE_TITLE_POLICY="titlePolicy"</code> */
    public static final String PREFERENCE_TITLE_POLICY = "titlePolicy";
    /** Constant <code>PREFERENCE_DESCRIPTION_POLICY="descriptionPolicy"</code> */
    public static final String PREFERENCE_DESCRIPTION_POLICY = "descriptionPolicy";
    /** Constant <code>DEFAULT_ANTISAMY_POLICY="antisamy-textonly"</code> */
    public static final String DEFAULT_ANTISAMY_POLICY = "antisamy-textonly";

    private RomeNewsProcessorImpl processor;
    private CloseableHttpClient httpClient;   // External configuration sets this one
    private HttpClient compressingClient;    // Internally we use this one

    private String proxyHost = null;
    private String proxyPort = null;
    private int connectionTimeout = 3000;   // Default connection timeout in ms
    private int readTimeout = 10000; // Default read timeout in milliseconds
    private long connectionManagerTimeout = 5000;  // Default timeout of getting connection from connection manager
    private int timesToRetry = 2;
    private String cacheKeyPrefix = "";  // default is no prefix

    /**
     * <p>Getter for the field <code>httpClient</code>.</p>
     *
     * @return a {@link org.apache.http.impl.client.CloseableHttpClient} object
     */
    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * <p>Setter for the field <code>httpClient</code>.</p>
     *
     * @param httpClient a {@link org.apache.http.impl.client.CloseableHttpClient} object
     */
    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * <p>Setter for the field <code>proxyHost</code>.</p>
     *
     * @param proxyHost a {@link java.lang.String} object
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * <p>Setter for the field <code>proxyPort</code>.</p>
     *
     * @param proxyPort a {@link java.lang.String} object
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * <p>Getter for the field <code>connectionTimeout</code>.</p>
     *
     * @return a int
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * <p>Setter for the field <code>connectionTimeout</code>.</p>
     *
     * @param connectionTimeout a int
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * <p>Getter for the field <code>readTimeout</code>.</p>
     *
     * @return a int
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * <p>Setter for the field <code>readTimeout</code>.</p>
     *
     * @param readTimeout a int
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * <p>Getter for the field <code>connectionManagerTimeout</code>.</p>
     *
     * @return a long
     */
    public long getConnectionManagerTimeout() {
        return connectionManagerTimeout;
    }

    /**
     * <p>Setter for the field <code>connectionManagerTimeout</code>.</p>
     *
     * @param connectionManagerTimeout a long
     */
    public void setConnectionManagerTimeout(long connectionManagerTimeout) {
        this.connectionManagerTimeout = connectionManagerTimeout;
    }

    /**
     * <p>Getter for the field <code>timesToRetry</code>.</p>
     *
     * @return a int
     */
    public int getTimesToRetry() {
        return timesToRetry;
    }

    /**
     * <p>Setter for the field <code>timesToRetry</code>.</p>
     *
     * @param timesToRetry a int
     */
    public void setTimesToRetry(int timesToRetry) {
        this.timesToRetry = timesToRetry;
    }

    /**
     * <p>Setter for the field <code>processor</code>.</p>
     *
     * @param processor a {@link org.jasig.portlet.newsreader.processor.RomeNewsProcessorImpl} object
     */
    public void setProcessor(RomeNewsProcessorImpl processor) {
        this.processor = processor;
    }

    /**
     * Cache prefix to support multiple instances of this class using the same cache.
     *
     * @param prefix    Unique prefix for cache entries for this particular instance
     */
    public void setCacheKeyPrefix(String prefix) {
        this.cacheKeyPrefix = prefix;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    /**
     * <p>init.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void init() throws Exception {
        String proxyHost = null;
        String proxyPort = null;

        if (StringUtils.isBlank(this.proxyHost) && StringUtils.isBlank(this.proxyPort)) {
            log.trace("Checking for proxy configuration from system properties...");
            proxyHost = System.getProperty("http.proxyHost");
            proxyPort = System.getProperty("http.proxyPort");
            if (StringUtils.isNotBlank(proxyHost) && StringUtils.isNotBlank(proxyPort)) {
                log.debug("Found proxy configuration from system properties");
            }
        } else {
            	log.debug("Using proxy settings from fields set during bean construction");
            	proxyHost = this.proxyHost;
            	proxyPort = this.proxyPort;
         }

        if (!StringUtils.isBlank(proxyHost) && !StringUtils.isBlank(proxyPort)) {
            HttpHost proxy = new HttpHost(proxyHost, Integer.valueOf(proxyPort));
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            log.debug("Using proxy configuration to retrieve news feeds: " + proxyHost + ":" + proxyPort);
        } else {
            log.debug("No proxy configuration is set. Proceeding normally...");
        }

        // Spring configuration prevents us from using type AbstractHttpClient because we are wrapping the HTTP Client
        // with DecompressingHttpClient which only implements HttpClient, so sadly we're getting around it with a
        // second field.
        compressingClient = new DecompressingHttpClient(httpClient);
    }

    /**
     * <p>getPolicyPref.</p>
     *
     * @param policyKey a {@link java.lang.String} object
     * @return an array of {@link java.lang.String} objects
     */
    protected String[] getPolicyPref(String... policyKey) {
        String[] values = new String[policyKey.length];
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes instanceof PortletRequestAttributes) {
                PortletRequest request = ((PortletRequestAttributes) requestAttributes).getRequest();
                PortletPreferences prefs = request.getPreferences();
                List<String> valueList = new ArrayList<String>();
                for (String key : policyKey) {
                    valueList.add(prefs.getValue(key, DEFAULT_ANTISAMY_POLICY));
                }
                return valueList.toArray(values);
            }
        } catch (IllegalStateException ex) {
            log.warn("Call to RomeAdapter.getPolicyPref that was not within a request", ex);
        }
        /* In other words, if we're not in a PortletRequest you get
         * the DEFAULT_ANTISAMY_POLICY for every value requested.
         */
        Arrays.fill(values, DEFAULT_ANTISAMY_POLICY);
        return values;
    }

    /* (non-Javadoc)
      * @see org.jasig.portlet.newsreader.adapter.INewsAdapter#getSyndFeed(org.jasig.portlet.newsreader.NewsConfiguration, javax.portlet.PortletRequest)
      */
    /** {@inheritDoc} */
    @Override
    public PaginatingNewsFeed getSyndFeed(NewsConfiguration config, int page, int maxStories) throws NewsException {

        PaginatingNewsFeed feed = null;

        // Look for an alternative AntiSamy policy file in the portlet preferences. If found, use it
        // otherwise use the default policyFile being injected into this class via Spring.
        String policy[] = getPolicyPref(PREFERENCE_TITLE_POLICY, PREFERENCE_DESCRIPTION_POLICY);
        String titlePolicy = policy[0];
        String descriptionPolicy = policy[1];

        // Get the URL for this feed
        // If there is a 2nd URL, it is a fall-back in case the first does not work.
        // Using two URLs is handy if your first URL happens to be a local file cache
        // of the feed that is being maintained by an external script. This way developers
        // can still view/test the feed even if they don't have a local file cache.
        String url = config.getNewsDefinition().getParameters().get("url");

        if ( url == null )
        {
            throw new IllegalArgumentException( "The url parameter was not found for " + config.getNewsDefinition().getName() + " this is a required portlet preference." );
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
                feed = getSyndFeed(url, titlePolicy, descriptionPolicy, maxStories);
            }
            else
            {
                // Two URLs, so if the first fails, try the backup...
                try
                {
                    feed = getSyndFeed(url, titlePolicy, descriptionPolicy, maxStories);
                }
                catch ( NewsException ex )
                {
                    log.warn( "Failed to load feed at the primary URL so trying URL2", ex );
                }

                if ( feed == null )
                {
                    // there must not be a local file cache, or it failed, so try the real url...
                    feed = getSyndFeed(url2, titlePolicy, descriptionPolicy, maxStories);
                }
            }

            // save the feed to the cache
            cachedElement = new Element(key, feed);
            cache.put(cachedElement);
        } else {
            log.debug("Cache hit");
            feed = (PaginatingNewsFeed) cachedElement.getObjectValue();
            if (maxStories != feed.getMaxStories()) {
                // Clear cache and recreate by recursive call
                cache.remove(key);
                return getSyndFeed(config, page, maxStories);
            }
        }

        if (feed != null) feed.setPage(page);

        // return the event list or null if the feed was not available.
        return feed;
    }

    /**
     * Retrieve the entire feed using HTTPClient and clean using AntiSamy,
     * build an SyndFeed object using ROME.
     *
     * @param url        String of the feed to be retrieved
     * @param titlePolicy String the cleaning policy for the title
     * @param descriptionPolicy String the cleaning policy for the description
     * @param maxStories        limit number of news items
     * @return SyndFeed Feed object
     * @throws org.jasig.portlet.newsreader.adapter.NewsException if any.
     */
    protected PaginatingNewsFeed getSyndFeed(String url, String titlePolicy, String descriptionPolicy, int maxStories) throws NewsException {

        HttpGet get = null;
        PaginatingNewsFeed feed = null;
        InputStream in = null;

        try {

            log.debug("Retrieving feed " + url);

            get = new HttpGet(url);
            get.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");
            HttpResponse httpResponse = compressingClient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.warn("HttpStatus for " + url + ":" + httpResponse);
            }

            // retrieve
            in = httpResponse.getEntity().getContent();

            // See if we got back any results. If so, then we can work on the results.
            // Otherwise we'd eat a parse error for trying to parse a null stream.
            if ( in != null )
            {
                feed = processor.getFeed(in, titlePolicy, descriptionPolicy, maxStories);
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
        key.append(this.cacheKeyPrefix);
        key.append(url);
        return key.toString();
    }

    private Cache cache;

    /**
     * <p>Setter for the field <code>cache</code>.</p>
     *
     * @param cache a {@link net.sf.ehcache.Cache} object
     */
    public void setCache(Cache cache) {
        this.cache = cache;
    }

}
