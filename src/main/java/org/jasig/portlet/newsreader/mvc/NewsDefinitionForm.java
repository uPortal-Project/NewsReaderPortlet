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

package org.jasig.portlet.newsreader.mvc;

import java.util.*;
import java.util.Map.Entry;

/*
 * @author Anthony Colebourne
 */
public class NewsDefinitionForm {

    private Long id = new Long(-1);
    private String className;
    private String name;
    private Set<String> role = new HashSet<String>();
    private List<String> parameterName = new ArrayList<String>();
    private List<String> parameterValue = new ArrayList<String>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public List<String> getParameterName() {
        return parameterName;
    }

    public void setParameterName(List<String> parameterName) {
        this.parameterName = parameterName;
    }

    public List<String> getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(List<String> parameterValue) {
        this.parameterValue = parameterValue;
    }

    public void addParameter(Entry<String, String> entry) {
        this.parameterName.add(entry.getKey());
        this.parameterValue.add(entry.getValue());
    }

    public void addParameters(Map<String, String> map) {
        Set<Entry<String, String>> entries = map.entrySet();
        for (Entry entry : entries) {
            this.addParameter(entry);
        }
    }

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
