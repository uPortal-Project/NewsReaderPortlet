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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.AbstractView;

/**
 * <p>EmptyView class.</p>
 *
 * @author bgonzalez
 * @since 5.1.1
 */
public class EmptyView extends AbstractView {

    /**
     * <p>Constructor for EmptyView.</p>
     */
    public EmptyView() {
        setContentType(DEFAULT_CONTENT_TYPE);
    }

    /**
     * Default content type. Overridable as bean property.
     */
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonEncoding encoding = JsonEncoding.UTF8;

    private Set<String> renderedAttributes;

    /**
     * Sets the {@code ObjectMapper} for this view. If not set, a default {@link com.fasterxml.jackson.databind.ObjectMapper#ObjectMapper() ObjectMapper}
     * is used.
     *
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further control of the JSON serialization
     * process. For example, an extended {@link com.fasterxml.jackson.databind.ser.SerializerFactory} can be configured that provides custom serializers for
     * specific types. The other option for refining the serialization process is to use Jackson's provided annotations on
     * the types to be serialized, in which case a custom-configured ObjectMapper is unnecessary.
     *
     * @param objectMapper a {@link com.fasterxml.jackson.databind.ObjectMapper} object
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "'objectMapper' must not be null");
        this.objectMapper = objectMapper;
    }

    /**
     * Sets the {@code JsonEncoding} for this converter. By default, {@linkplain JsonEncoding#UTF8 UTF-8} is used.
     *
     * @param encoding a {@link com.fasterxml.jackson.core.JsonEncoding} object
     */
    public void setEncoding(JsonEncoding encoding) {
        Assert.notNull(encoding, "'encoding' must not be null");
        this.encoding = encoding;
    }

    /**
     * Sets the attributes in the model that should be rendered by this view. When set, all other model attributes will be
     * ignored.
     *
     * @param renderedAttributes a {@link java.util.Set} object
     */
    public void setRenderedAttributes(Set<String> renderedAttributes) {
        this.renderedAttributes = renderedAttributes;
    }

    /** {@inheritDoc} */
    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(getContentType());
        response.setCharacterEncoding(encoding.getJavaName());
    }

    /** {@inheritDoc} */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JsonGenerator generator =
            objectMapper.getJsonFactory().createJsonGenerator(response.getOutputStream(), encoding);
        generator.writeRaw("    ");
    }

    /**
     * Filters out undesired attributes from the given model. The return value can be either another {@link java.util.Map}, or a
     * single value object.
     *
     * <p>Default implementation removes {@link org.springframework.validation.BindingResult} instances and entries not included in the {@link
     *#setRenderedAttributes(Set) renderedAttributes} property.
     *
     * @param model the model, as passed on to {@link #renderMergedOutputModel}
     * @return the object to be rendered
     */
    protected Object filterModel(Map<String, Object> model) {
        Map<String, Object> result = new HashMap<String, Object>(model.size());
        Set<String> renderedAttributes =
                !CollectionUtils.isEmpty(this.renderedAttributes) ? this.renderedAttributes : model.keySet();
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!(entry.getValue() instanceof BindingResult) && renderedAttributes.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
}
