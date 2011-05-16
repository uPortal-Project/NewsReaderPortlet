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

package org.jasig.portlet.newsreader.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;

/**
 * @author Jen Bourey
 * @version $Revision$
 */
public class ViewResolverImpl implements IViewResolver {

    private List<Pattern> mobileDeviceRegexes = null;
    
    /**
     * Set a list of regex patterns for user agents which should be considered
     * to be mobile devices.
     * 
     * @param patterns
     */
    public void setMobileDeviceRegexes(List<String> patterns) {
            this.mobileDeviceRegexes = new ArrayList<Pattern>();
            for (String pattern : patterns) {
                    this.mobileDeviceRegexes.add(Pattern.compile(pattern));
            }
    }

    public String getSingleFeedView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewSingleFeed-jQM";
        } else {
            return "viewSingleFeed";
        }
    }

    public String getReaderView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewNews-jQM";
        } else {
            return "viewNews";
        }
    }
    
    public String getPreferencesView(PortletRequest request) {
        if (isMobile(request)) {
            return "editNews-jQM";
        } else {
            return "editNews";
        }
    }
    
    protected boolean isMobile(PortletRequest request) {
        
        String userAgent = request.getProperty("user-agent");
        if (this.mobileDeviceRegexes != null && userAgent != null) {
            for (Pattern regex : this.mobileDeviceRegexes) {
                    if (regex.matcher(userAgent).matches()) {
                            return true;
                    }
            }
        }
        return false;

    }
    
}
