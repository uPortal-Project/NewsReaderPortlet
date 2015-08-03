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

import com.sun.syndication.feed.synd.SyndEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.model.NewsFeedItem;
import org.jasig.portlet.newsreader.model.RemoteHttpFullStory;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * Strategy implementation derived from {@link RomeNewsProcessorImpl} with support for full story.
 *
 * Rather than set the {@code link} in the {@link NewsFeedItem}, a {@link RemoteHttpFullStory} is created
 * using the link as the URL.
 *
 * @author Benito J. Gonzalez <bgonzalez@unicon.net>
 * @since 3.1.2
 */
public class RomeNewsFullStoryProcessorImpl extends RomeNewsProcessorImpl {

    protected final Log log = LogFactory.getLog(getClass());

    protected NewsFeedItem getNewsFeedItem(SyndEntry entry, String titlePolicy, String descriptionPolicy) throws PolicyException, ScanException {
        log.debug("getNewsFeedItem() in full story method");
        NewsFeedItem item = super.getNewsFeedItem(entry, titlePolicy, descriptionPolicy);
        RemoteHttpFullStory fullStory = new RemoteHttpFullStory(entry.getLink());
        // Here would be a good place to pass along a sequence of filters to
        // RemoteHttpFullStory
        item.setFullStory(fullStory);
        item.setLink(null);
        return item;
    }

}
