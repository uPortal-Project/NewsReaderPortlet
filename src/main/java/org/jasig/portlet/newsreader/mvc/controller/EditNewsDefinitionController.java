/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.mvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.NewsDefinitionForm;
import org.jasig.portlet.newsreader.service.UnsharedNewsSetServiceImpl;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;


/**
 * EditNewsDefinitionController provides a GUI for adding and editing
 * predefined newss.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class EditNewsDefinitionController extends SimpleFormController {

    private static Log log = LogFactory
            .getLog(EditNewsDefinitionController.class);

    public EditNewsDefinitionController() {
    }

    /*
      * (non-Javadoc)
      * @see org.springframework.web.portlet.mvc.AbstractFormController#formBackingObject(javax.portlet.PortletRequest)
      */
    protected Object formBackingObject(PortletRequest request) throws Exception {
        // if we're editing a news, retrieve the news definition from
        // the database and add the information to the form
        String id = request.getParameter("id");
        if (id != null && !id.equals("")) {
            Long definitionId = Long.parseLong(id);
            if (definitionId > -1) {
                PredefinedNewsDefinition definition = newsStore.getPredefinedNewsDefinition(definitionId);
                NewsDefinitionForm command = new NewsDefinitionForm();
                command.setId(definition.getId());
                command.setName(definition.getName());
                command.setClassName(definition.getClassName());
                command.setRole(definition.getDefaultRoles());
                command.addParameters(definition.getParameters());
                return command;
            } else {
                // otherwise, construct a brand new form
                // create the form
                return new NewsDefinitionForm();
            }

        } else {
            // otherwise, construct a brand new form
            // create the form
            return new NewsDefinitionForm();
        }
    }

    @Override
    protected void onSubmitAction(ActionRequest request,
                                  ActionResponse response, Object command, BindException errors)
            throws Exception {

        // get the form data
        NewsDefinitionForm form = (NewsDefinitionForm) command;

        // construct a news definition from the form data
        PredefinedNewsDefinition definition = null;

        // If an id was submitted, retrieve the news definition we're
        // trying to edit.  Otherwise, create a new definition.
        if (form.getId() > -1)
            definition = newsStore.getPredefinedNewsDefinition(form.getId());
        else
            definition = new PredefinedNewsDefinition();

        // set the news definition properties based on the
        // submitted form
        definition.setClassName(form.getClassName());
        definition.setDefaultRoles(form.getRole());
        definition.setName(form.getName());
        definition.setParameters(form.getParameters());

        // save the news definition
        newsStore.storeNewsDefinition(definition);

        // send the user back to the main administration page
        response.setRenderParameter("action", "administration");

    }

    private NewsStore newsStore;

    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }
    

}