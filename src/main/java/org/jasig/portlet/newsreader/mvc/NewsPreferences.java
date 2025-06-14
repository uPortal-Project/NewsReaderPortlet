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
package org.jasig.portlet.newsreader.mvc;

/*
 * @author Anthony Colebourne
 */
/**
 * <p>NewsPreferences class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class NewsPreferences {

    private String showNews;
    private String[] newsUrls;

    /**
     * <p>Constructor for NewsPreferences.</p>
     */
    public NewsPreferences() {
    }

    /**
     * <p>Getter for the field <code>newsUrls</code>.</p>
     *
     * @return an array of {@link java.lang.String} objects
     */
    public String[] getNewsUrls() {
        return newsUrls;
    }

    /**
     * <p>Setter for the field <code>newsUrls</code>.</p>
     *
     * @param newsUrls an array of {@link java.lang.String} objects
     */
    public void setNewsUrls(String[] newsUrls) {
        this.newsUrls = newsUrls;
    }

    /**
     * <p>Getter for the field <code>showNews</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getShowNews() {
        return showNews;
    }

    /**
     * <p>Setter for the field <code>showNews</code>.</p>
     *
     * @param showNews a {@link java.lang.String} object
     */
    public void setShowNews(String showNews) {
        this.showNews = showNews;
    }

}
