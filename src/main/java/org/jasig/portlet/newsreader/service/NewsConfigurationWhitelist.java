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
package org.jasig.portlet.newsreader.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.NewsDefinition;
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;

/**
 * <p>NewsConfigurationWhitelist class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class NewsConfigurationWhitelist extends Whitelist {

	/** Constant <code>WHITELIST_CALLBACK</code> */
	public static final Whitelist.Callback<PredefinedNewsConfiguration> WHITELIST_CALLBACK =
            new Whitelist.Callback<PredefinedNewsConfiguration>() {
                @Override
                public String getFname(PredefinedNewsConfiguration item) {
                    final NewsDefinition def = item.getNewsDefinition();
                    if (def instanceof PredefinedNewsDefinition) {
                        PredefinedNewsDefinition predef = (PredefinedNewsDefinition) def;
                        return predef.getFname();
                    } else {
                        String msg = "PredefinedNewsConfiguration based on "
                                + "non-predefined NewsDefinition:  " + item;
                        throw new RuntimeException(msg);
                    }
                }
    };
	
    private static final String WHITELIST_USERFEEDS_PREFERENCE = "Whitelist.userFeeds";
	
    /*
     * This method iterates the items and asked the parent filter the same question several times. If we were to 
     * make Whitelist less generic then this calss could be removed and all the filtering coudl be done in 
     * one iteration of the set within Whitelist
     */
	/**
	 * <p>filter.</p>
	 *
	 * @param req a {@link javax.portlet.PortletRequest} object
	 * @param items a {@link java.util.Collection} object
	 * @return a {@link java.util.List} object
	 */
	public List<NewsConfiguration> filter(PortletRequest req, Collection<NewsConfiguration> items) {
		List<NewsConfiguration> rslt = new ArrayList<NewsConfiguration>();
        for (NewsConfiguration config : items) {
            if (config.getNewsDefinition().isPredefined()) {
                // Apply whitelist filtering on the pre-defined variety
                PredefinedNewsConfiguration predef = (PredefinedNewsConfiguration) config;
                List<PredefinedNewsConfiguration> filtered = super.filter(req, Collections.singleton(predef), WHITELIST_CALLBACK);
                if (filtered.size() == 0) {
                    // Item was excluded...
                    continue;
                }
                rslt.add(config);
            } else {
            	final PortletPreferences prefs = req.getPreferences();
                final String isWhitelistUserfeeds = prefs.getValue(WHITELIST_USERFEEDS_PREFERENCE, "true");
            	if(Boolean.valueOf(isWhitelistUserfeeds)) {
            		rslt.add(config);
            	}
            }
        }
        return rslt;
		
	}
}
