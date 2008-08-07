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

import java.util.HashMap;
import java.util.Map;

/**
 * NewsDefinition represents the base class for news registrations.
 * Information required to retrieve the news, such as the news's URL
 * or important default system-wide configuration information may be stored
 * in the parameters map.  In order to add this news for a specific user,
 * a NewsConfiguration referencing this news definition must be
 * created.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class NewsDefinition {

    private Long id = new Long(-1);
    private String className;
    private String name;
    private Map<String, String> parameters = new HashMap<String, String>();

    /**
     * Default constructor.
     */
    public NewsDefinition() {
        super();
    }

    public NewsDefinition(Long id, String className, String name) {
        this.id = id;
        this.className = className;
        this.name = name;
    }

    /**
     * Return the unique id of this news.
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique id for this news.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the name of the adapter class for this news which will
     * determine how the news is retrieved.  This id must match a
     * news adapter registered in the spring context files.
     *
     * @return
     */
    public String getClassName() {
        return className;
    }

    /**
     * Set the name of the adapter class for this news which will
     * determine how the news is retrieved.  This id must match a
     * news adapter registered in the spring context files.
     *
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Return the display name for this news.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the display name for this news.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the map of news parameters.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @return parameter map
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Set the map of news parameters.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @param parameters parameter map
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Add an individual news parameter.  These parameters can hold
     * any extra information needed by the particular adapter used by
     * this news, such as a URL.
     *
     * @param name  parameter name (key)
     * @param value value to be stored
     */
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
	}
	
	@Override
	public String toString() {
		return "id: " + this.id + ", class: " + this.className + ", name: " + this.name;
	}
	

	
}
