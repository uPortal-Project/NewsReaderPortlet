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
package org.jasig.portlet.newsreader.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

/**
 * Allows content to be filtered per portlet-definition.
 *
 * @author drewwills
 */
public class Whitelist<T> {

    /**
     * A multi-valued portlet preference that allows administrators to restrict
     * which PredefinedNewsDefinitions will appear in a particular portlet
     * definition.  In other words, you can publish the 'news' portlet multiple
     * times and divide up the pre-defined feeds any way you like.  The default
     * value for this preference is empty;  if the runtime value is is empty,
     * whitelist filtering will not be performed.
     */
    private static final String WHITELIST_REGEX_PREFERENCE = "Whitelist.regexValues";

    public List<T> filter(PortletRequest req, Collection<T> items, Callback<T> callback) {

        final PortletPreferences prefs = req.getPreferences();
        final String[] whitelistRegexValues = prefs.getValues(WHITELIST_REGEX_PREFERENCE, new String[0]);

        final List<T> rslt = new ArrayList<T>();
        if (whitelistRegexValues.length > 0) {
            // Convert Strings to Patterns
            final Set<Pattern> patterns = new HashSet<Pattern>();
            for (String regex : whitelistRegexValues) {
                patterns.add(Pattern.compile(regex));
            }
            // Filter out inputs that don't match at least one Pattern
            for (T item : items) {
                final String fname = callback.getFname(item);
                for (Pattern p : patterns) {
                    Matcher m = p.matcher(fname == null ? "" : fname);
                    if (m.matches()) {
                        rslt.add(item);
                        break;
                    }
                }
            }
        } else {
            // No filtering
            rslt.addAll(items);
        }

        return rslt;

    }

    /*
     * Nested Types
     */

    public interface Callback<T> {
        String getFname(T item);
    }
}
