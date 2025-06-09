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

import java.util.*;
import java.util.Map.Entry;

/*
 * @author Anthony Colebourne
 */
/**
 * <p>NewsDefinitionForm class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class NewsDefinitionForm {

    private Long id = new Long(-1);
    private String className;
    private String name;
    private Set<String> role = new HashSet<String>();
    private List<String> parameterName = new ArrayList<String>();
    private List<String> parameterValue = new ArrayList<String>();

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.Long} object
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.Long} object
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>className</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getClassName() {
        return className;
    }

    /**
     * <p>Setter for the field <code>className</code>.</p>
     *
     * @param className a {@link java.lang.String} object
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>role</code>.</p>
     *
     * @return a {@link java.util.Set} object
     */
    public Set<String> getRole() {
        return role;
    }

    /**
     * <p>Setter for the field <code>role</code>.</p>
     *
     * @param role a {@link java.util.Set} object
     */
    public void setRole(Set<String> role) {
        this.role = role;
    }

    /**
     * <p>Getter for the field <code>parameterName</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getParameterName() {
        return parameterName;
    }

    /**
     * <p>Setter for the field <code>parameterName</code>.</p>
     *
     * @param parameterName a {@link java.util.List} object
     */
    public void setParameterName(List<String> parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * <p>Getter for the field <code>parameterValue</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<String> getParameterValue() {
        return parameterValue;
    }

    /**
     * <p>Setter for the field <code>parameterValue</code>.</p>
     *
     * @param parameterValue a {@link java.util.List} object
     */
    public void setParameterValue(List<String> parameterValue) {
        this.parameterValue = parameterValue;
    }

    /**
     * <p>addParameter.</p>
     *
     * @param entry a {@link java.util.Map.Entry} object
     */
    public void addParameter(Entry<String, String> entry) {
        this.parameterName.add(entry.getKey());
        this.parameterValue.add(entry.getValue());
    }

    /**
     * <p>addParameters.</p>
     *
     * @param map a {@link java.util.Map} object
     */
    public void addParameters(Map<String, String> map) {
        Set<Entry<String, String>> entries = map.entrySet();
        for (Entry<String,String> entry : entries) {
            this.addParameter(entry);
        }
    }

    /**
     * <p>getParameters.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> getParameters() {

        // create a new map to hold our parameters in
        Map<String, String> map = new HashMap<String, String>();

        // add each parameter to the map
        int pos = 0;
        for (String key : this.parameterName) {
            map.put(key, this.parameterValue.get(pos));
            pos++;
        }

        return map;

    }

}
