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

package org.jasig.portlet.newsreader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * PredefinedNewsConfiguration represents a user configuration of a built-in
 * news definition.  There may be many configurations for each predefined
 * news definition.  This class defines an extra place to put user-specific
 * configuration information and preferences.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class PredefinedNewsConfiguration extends NewsConfiguration {

    private static Log log = LogFactory.getLog(PredefinedNewsConfiguration.class);

    private PredefinedNewsDefinition newsDefinition;
    private Map<String, String> preferences = new HashMap<String, String>();

    /**
     * Default constructor
     */
    public PredefinedNewsConfiguration() {
        super();
        this.newsDefinition = new PredefinedNewsDefinition();
    }

    /**
     * Get the user-specific preferences for this configuration.
     *
     * @return
     */
    public Map<String, String> getPreferences() {
        return preferences;
    }

    /**
     * Set the user-specific preferences for this configuration.
     *
     * @param preferences
     */
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    /**
     * Add a user preference for this configuration.
     *
     * @param name  parameter name (key)
     * @param value value to be stored
     */
    public void addPreference(String name, String value) {
        this.preferences.put(name, value);
    }

    /*
      * (non-Javadoc)
      * @see edu.yale.its.tp.portlets.news.NewsConfiguration#getNewsDefinition()
      */
    public PredefinedNewsDefinition getNewsDefinition() {
        return newsDefinition;
    }

    /*
      * (non-Javadoc)
      * @see edu.yale.its.tp.portlets.news.NewsConfiguration#setNewsDefinition(edu.yale.its.tp.portlets.news.NewsDefinition)
      */
    public void setNewsDefinition(PredefinedNewsDefinition definition) {
        this.newsDefinition = definition;
	}
	
}
