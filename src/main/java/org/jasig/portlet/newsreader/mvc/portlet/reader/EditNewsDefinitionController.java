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

package org.jasig.portlet.newsreader.mvc.portlet.reader;

import java.util.List;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.adapter.INewsAdapter;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.NewsDefinitionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


/**
 * EditNewsDefinitionController provides a GUI for adding and editing
 * predefined newss.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Controller
@RequestMapping("EDIT")
public class EditNewsDefinitionController {

    protected final Log log = LogFactory.getLog(getClass());

    private NewsStore newsStore;

    @Resource(name="availableAdapters")
    private List<INewsAdapter> availableAdapters;

    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    @ModelAttribute("newsDefinitionForm")
    public NewsDefinitionForm getNewsForm(PortletRequest request) throws Exception {
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

    @RenderMapping(params = "action=editNewsDefinition")
    public String getAdminNewsEditView() {
        return "editNewsDefinition";
    }
    
    @ActionMapping(params = "action=editNewsDefinition")
    public void onSubmitAction(ActionRequest request,
                                  ActionResponse response, NewsDefinitionForm form)
            throws Exception {

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
    
    @ModelAttribute("availableAdapters")
    public List<INewsAdapter> getAvailableAdapters() {
        return availableAdapters;
    }

}