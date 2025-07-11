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
package org.jasig.portlet.newsreader.adapter;

/**
 * <p>Abstract AbstractNewsAdapter class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public abstract class AbstractNewsAdapter implements INewsAdapter {
    
    private static final String NAME_KEY_SUFFIX = ".messages.name";
    private static final String DESCRIPTION_KEY_SUFFIX = ".messages.description";

    /** {@inheritDoc} */
    @Override
    public String getClassName() {
        return getClass().getName();
    }

    /** {@inheritDoc} */
    @Override
    public String getNameKey() {
        return this.getClassName() + NAME_KEY_SUFFIX;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescriptionKey() {
        return this.getClassName() + DESCRIPTION_KEY_SUFFIX;
    }

}
