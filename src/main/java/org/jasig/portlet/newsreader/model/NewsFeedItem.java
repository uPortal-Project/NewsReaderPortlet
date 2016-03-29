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
package org.jasig.portlet.newsreader.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rometools.rome.feed.synd.SyndPerson;

/**
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
public class NewsFeedItem  implements Comparable<NewsFeedItem> {

    private String imageUrl;
    private List<SyndPerson> authors;
    private String description;
    private String content;
    private String videoUrl;
    private String link;
    private String title;
    private String uri;
    private FullStory fullStory;
    private Date pubDate;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<SyndPerson> getAuthors() {
        return authors;
    }

    public void setAuthors(List<SyndPerson> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public FullStory getFullStory() {
		return fullStory;
	}

    public void setFullStory(FullStory fullStory) {
        this.fullStory = fullStory;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public int compareTo(NewsFeedItem that) {
        if (this.pubDate == null) {
            return that.pubDate == null ? 0 : -1;
        } else {
            return that.pubDate == null ? 1 : this.pubDate.compareTo(that.pubDate);
        }
    }
}
