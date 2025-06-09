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
package org.jasig.portlet.newsreader;

/**
 * UserDefinedNewsDefinition represents a user-added news definition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
public class UserDefinedNewsDefinition extends NewsDefinition {

    private UserDefinedNewsConfiguration userConfiguration;

    /**
     * Default constructor
     */
    public UserDefinedNewsDefinition() {
        super();
    }

    /**
     * Construct a new user-defined news definition
     *
     * @param id a {@link java.lang.Long} object
     * @param className a {@link java.lang.String} object
     * @param name a {@link java.lang.String} object
     */
    public UserDefinedNewsDefinition(Long id, String className, String name) {
        super(id, className, name);
    }

    /*
      * (non-Javadoc)
      * @see java.lang.Object#toString()
      */
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "id: " + getId() + ", name: " + getName() + ", parameters: "
                + getParameters().toString();
    }

    /**
     * Get the news configuration for this definition.
     *
     * @return a {@link org.jasig.portlet.newsreader.UserDefinedNewsConfiguration} object
     */
    public UserDefinedNewsConfiguration getUserConfiguration() {
        return userConfiguration;
    }

    /**
     * Set the news configuration for this definition.
     *
     * @param userConfiguration a {@link org.jasig.portlet.newsreader.UserDefinedNewsConfiguration} object
     */
    public void setUserConfiguration(
            UserDefinedNewsConfiguration userConfiguration) {
        this.userConfiguration = userConfiguration;
	}


}
