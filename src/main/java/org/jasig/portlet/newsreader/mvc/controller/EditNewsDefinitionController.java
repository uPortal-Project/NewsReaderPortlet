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

package org.jasig.portlet.newsreader.mvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.NewsDefinitionForm;
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