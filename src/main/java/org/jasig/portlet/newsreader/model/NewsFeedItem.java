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
package org.jasig.portlet.newsreader.model;

import java.util.Date;
import java.util.List;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndPerson;

/**
 * <p>NewsFeedItem class.</p>
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @since 5.1.1
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
    private List<SyndCategory> categories;

    /**
     * <p>Getter for the field <code>imageUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * <p>Setter for the field <code>imageUrl</code>.</p>
     *
     * @param imageUrl a {@link java.lang.String} object
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * <p>Getter for the field <code>authors</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<SyndPerson> getAuthors() {
        return authors;
    }

    /**
     * <p>Setter for the field <code>authors</code>.</p>
     *
     * @param authors a {@link java.util.List} object
     */
    public void setAuthors(List<SyndPerson> authors) {
        this.authors = authors;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link java.lang.String} object
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>Getter for the field <code>videoUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * <p>Setter for the field <code>videoUrl</code>.</p>
     *
     * @param videoUrl a {@link java.lang.String} object
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     * <p>Getter for the field <code>link</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLink() {
        return link;
    }

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link java.lang.String} object
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>uri</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUri() {
        return uri;
    }

    /**
     * <p>Setter for the field <code>uri</code>.</p>
     *
     * @param uri a {@link java.lang.String} object
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * <p>Getter for the field <code>fullStory</code>.</p>
     *
     * @return a {@link org.jasig.portlet.newsreader.model.FullStory} object
     */
    public FullStory getFullStory() {
		return fullStory;
	}

    /**
     * <p>Setter for the field <code>fullStory</code>.</p>
     *
     * @param fullStory a {@link org.jasig.portlet.newsreader.model.FullStory} object
     */
    public void setFullStory(FullStory fullStory) {
        this.fullStory = fullStory;
    }

    /**
     * <p>Getter for the field <code>pubDate</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getPubDate() {
        return pubDate;
    }

    /**
     * <p>Setter for the field <code>pubDate</code>.</p>
     *
     * @param pubDate a {@link java.util.Date} object
     */
    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    /**
     * <p>Getter for the field <code>categories</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<SyndCategory> getCategories() {
        return categories;
    }

    /**
     * <p>Setter for the field <code>categories</code>.</p>
     *
     * @param categories a {@link java.util.List} object
     */
    public void setCategories(List<SyndCategory> categories) {
        this.categories = categories;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(NewsFeedItem that) {
        if (this.pubDate == null) {
            return that.pubDate == null ? 0 : -1;
        } else {
            return that.pubDate == null ? 1 : this.pubDate.compareTo(that.pubDate);
        }
    }
}
