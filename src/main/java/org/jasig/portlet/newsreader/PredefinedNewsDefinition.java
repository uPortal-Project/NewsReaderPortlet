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

import java.util.HashSet;
import java.util.Set;

/**
 * PredefinedNewsDefinition represents a built-in news definition.  These
 * definitions are defined in the database by an administrator, and can be
 * automatically added to user's news registration lists based on user
 * role.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class PredefinedNewsDefinition extends NewsDefinition {

    private Set<PredefinedNewsConfiguration> userConfigurations = new HashSet<PredefinedNewsConfiguration>();
    private Set<String> defaultRoles;

    /**
     * Default constructor
     */
    public PredefinedNewsDefinition() {
        super();
    }

    /**
     * Construct a new predefined news definition
     *
     * @param id
     * @param className
     * @param name
     */
    public PredefinedNewsDefinition(Long id, String className, String name) {
        super(id, className, name);
    }

    /**
     * Get all the user configurations for this news definition
     *
     * @return
     */
    public Set<PredefinedNewsConfiguration> getUserConfigurations() {
        return userConfigurations;
    }

    /**
     * Set the user configurations for this news definition
     *
     * @param configurations
     */
    public void setUserConfigurations(Set<PredefinedNewsConfiguration> configurations) {
        this.userConfigurations = configurations;
    }

    /**
     * Get the set of user roles who should get this news by default.
     *
     * @return set of default user roles
     */
    public Set<String> getDefaultRoles() {
        return defaultRoles;
    }

    /**
     * Set the user roles should should get this news by default.
     *
     * @param defaultRoles set of default user roles
     */
    public void setDefaultRoles(Set<String> defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    /**
     * Add a user configuration for this news definition
     *
     * @param config
     */
    public void addUserConfiguration(PredefinedNewsConfiguration config) {
        this.userConfigurations.add(config);
    }

    /**
     * Add a user role to the set of roles that should get this news
     * by default.
     *
     * @param role user role to be added
     */
	public void addDefaultRole(String role) {
		this.defaultRoles.add(role);
	}
	
}
