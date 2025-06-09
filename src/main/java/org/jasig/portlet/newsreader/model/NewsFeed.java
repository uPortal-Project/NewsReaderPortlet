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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * <p>NewsFeed class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class NewsFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<NewsFeedItem> entries = new ArrayList<>();
    private String author;
    private String link;
    private String title;
    private String copyright;

    /**
     * <p>Getter for the field <code>entries</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<NewsFeedItem> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * <p>Setter for the field <code>entries</code>.</p>
     *
     * @param entries a {@link java.util.List} object
     */
    public void setEntries(List<NewsFeedItem> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
    }

    /**
     * <p>Getter for the field <code>author</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getAuthor() {
        return author;
    }

    /**
     * <p>Setter for the field <code>author</code>.</p>
     *
     * @param author a {@link java.lang.String} object
     */
    public void setAuthor(String author) {
        this.author = author;
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
     * <p>Getter for the field <code>copyright</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * <p>Setter for the field <code>copyright</code>.</p>
     *
     * @param copyright a {@link java.lang.String} object
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NewsFeed)) {
            return false;
        }

        NewsFeed feed = (NewsFeed) obj;
        return new EqualsBuilder()
            .append(this.title, feed.title)
            .append(this.link, feed.link)
            .append(this.author, feed.author)
            .append(this.copyright, feed.copyright)
            .append(this.entries, feed.entries)
            .isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(464270933, -1074792143)
            .append(this.title)
            .append(this.link)
            .append(this.author)
            .append(this.copyright)
            .append(this.entries)
            .toHashCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("title", this.title)
                .append("link", this.link)
                .append("author", this.author)
                .append("copyright", this.copyright)
                .append("entries", this.entries)
                .toString();
    }

}
