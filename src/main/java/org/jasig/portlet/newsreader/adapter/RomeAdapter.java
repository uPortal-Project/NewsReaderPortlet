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

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.owasp.validator.html.*;

import javax.portlet.PortletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * RomeAdapter is a NewsAdapter for standard RSS and ATOM feeds available
 * online via http or https.
 * <p/>
 * Note: This class can process and feed supported by ROME https://rome.dev.java.net/.
 *
 * @author Anthony Colebourne
 */
public class RomeAdapter implements INewsAdapter {

    private static Log log = LogFactory.getLog(RomeAdapter.class);

    /* (non-Javadoc)
      * @see org.jasig.portlet.newsreader.adapter.INewsAdapter#getSyndFeed(org.jasig.portlet.newsreader.NewsConfiguration, javax.portlet.PortletRequest)
      */
    public SyndFeed getSyndFeed(NewsConfiguration config, PortletRequest request) throws NewsException {

        SyndFeed feed = null;

        // get the URL for this feed
        String url = config.getNewsDefinition().getParameters().get("url");

        // try to get the feed news
        String key = getCacheKey(url);
        Element cachedElement = cache.get(key);
        if (cachedElement == null) {

            log.debug("Cache miss");

            feed = getSyndFeed(url, request.getPortletSession().getPortletContext().getRealPath("/") + policyFile);

            // save the feed to the cache
            cachedElement = new Element(key, feed);
            cache.put(cachedElement);
        } else {
            log.debug("Cache hit");
            feed = (SyndFeed) cachedElement.getValue();
        }

        // return the event list
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
    protected SyndFeed getSyndFeed(String url, String policyFile) throws NewsException {

        SyndFeedInput input = new SyndFeedInput();
        HttpClient client = new HttpClient();
        GetMethod get = null;

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

            //parse
            SyndFeed feed = input.build(new XmlReader(in));

            //clean
            AntiSamy as = new AntiSamy();

            List<SyndEntry> a = feed.getEntries();

            for (SyndEntry entry : a) {
                SyndContent description = entry.getDescription();

                Policy policy = Policy.getInstance(policyFile);

                CleanResults cr = as.scan(description.getValue(), policy);
                description.setValue(cr.getCleanHTML());
                entry.setDescription(description);
                log.info("Feed " + url + " cleaned in " + cr.getScanTime() + " seconds");
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

    private String policyFile;

    public void setPolicyFile(String policyFile) {
        this.policyFile = policyFile;
	}
	
}
