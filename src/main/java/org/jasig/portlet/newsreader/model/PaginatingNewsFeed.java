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

import java.util.Collections;
import java.util.List;
import org.springframework.beans.support.PagedListHolder;

/**
 * <p>PaginatingNewsFeed class.</p>
 *
 * @author Chris White (christopher.white@manchester.ac.uk)
 * @since 5.1.1
 */
public class PaginatingNewsFeed extends NewsFeed {

    private static final long serialVersionUID = 1L;

    private final PagedListHolder<NewsFeedItem> holder = new PagedListHolder<NewsFeedItem>();
    private int maxStories = -1;
    // need to track page separate from holder due to holder.setPage(int)/.getPage() staying within last page
    private int page = 0;

    /**
     * <p>Constructor for PaginatingNewsFeed.</p>
     *
     * @param entriesPerPage a int
     */
    public PaginatingNewsFeed(int entriesPerPage) {
        this(entriesPerPage, 0);
    }

    /**
     * <p>Constructor for PaginatingNewsFeed.</p>
     *
     * @param entriesPerPage a int
     * @param initialPage a int
     */
    public PaginatingNewsFeed(int entriesPerPage, int initialPage) {
        this.page = initialPage;
        holder.setPage(page);
        holder.setPageSize(entriesPerPage);
        holder.setSource(super.getEntries());
    }

    /**
     * <p>Getter for the field <code>page</code>.</p>
     *
     * @return a int
     */
    public int getPage() {
        return holder.getPage();
    }

    /**
     * <p>Setter for the field <code>page</code>.</p>
     *
     * @param p a int
     */
    public void setPage(int p) {
        this.page = p;
        holder.setPage(p);
    }

    /**
     * <p>getPageCount.</p>
     *
     * @return a double
     */
    public double getPageCount() {
        return holder.getPageCount();
    }

    /**
     * <p>Setter for the field <code>maxStories</code>.</p>
     *
     * @param maxStories a int
     */
    public void setMaxStories(int maxStories) {
        this.maxStories = maxStories;
    }

    /**
     * <p>Getter for the field <code>maxStories</code>.</p>
     *
     * @return a int
     */
    public int getMaxStories() {
        return this.maxStories;
    }

    /** {@inheritDoc} */
    @Override
    public List<NewsFeedItem> getEntries() {
        if (page < holder.getPageCount()) {  // using .getPage() was always returning a valid value, so never reaching empty set
            return holder.getPageList();
        }
        return Collections.emptyList();
    }
}
