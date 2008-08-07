/*
Copyright (c) 2008, News Reader Portlet Development Team
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

* Redistributions of source code must retain the above copyright notice, this list of conditions and the following
  disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
  disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of the News Reader Portlet Development Team nor the names of its contributors may be used to endorse or
  promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
