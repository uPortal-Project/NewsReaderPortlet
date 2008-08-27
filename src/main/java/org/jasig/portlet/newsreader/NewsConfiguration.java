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

/**
 * NewsConfiguration represents a user-specific registration and configuration
 * for a NewsDefinition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class NewsConfiguration {

    private Long id = new Long(-1);
    private NewsDefinition newsDefinition;
    private NewsSet newsSet;
    private boolean displayed = true;
    private boolean active = true;


    /**
     * Determine whether this feed is active.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether this feed should be displayed or hidden.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Determine whether this news should be displayed or hidden.
     *
     * @return
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * Set whether this news should be displayed or hidden.
     *
     * @param displayed
     */
    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    /**
     * Get the unique ID for this NewsConfiguration.
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique ID for this NewsConfiguration.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the NewsDefinition for the news being configured.
     *
     * @return
     */
    public NewsDefinition getNewsDefinition() {
        return newsDefinition;
    }

    /**
     * Set the NewsDefinition for the news being configured.
     *
     * @param definition
     */
	public void setNewsDefinition(NewsDefinition definition) {
		this.newsDefinition = definition;
	}

	public NewsSet getNewsSet() {
		return newsSet;
	}

	public void setNewsSet(NewsSet newsSet) {
		this.newsSet = newsSet;
	}
	
}
