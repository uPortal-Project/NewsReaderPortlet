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
import org.jasig.portlet.newsreader.NewsConfiguration;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.UserDefinedNewsDefinition;
import org.jasig.portlet.newsreader.adapter.RomeAdapter;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.jasig.portlet.newsreader.mvc.NewsListingCommand;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.mvc.SimpleFormController;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import java.util.Map;


/**
 * EditNewsDefinitionController allows a user to add or edit a user-defined
 * news definition.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class EditUserRomeController extends SimpleFormController {

    private static Log log = LogFactory.getLog(EditUserRomeController.class);


    public EditUserRomeController() {
    }

    /*
      * (non-Javadoc)
      *
      * @see org.springframework.web.portlet.mvc.AbstractFormController#formBackingObject(javax.portlet.PortletRequest)
      */
    protected Object formBackingObject(PortletRequest request) throws Exception {

        // if we're editing a news, retrieve the news definition from
        // the database and add the information to the form
        String id = request.getParameter("id");
        if (id != null && !id.equals("")) {
            Long configurationId = Long.parseLong(id);
            if (configurationId > -1) {
                NewsConfiguration listing = (NewsConfiguration) newsStore.getNewsConfiguration(configurationId);
                log.debug("retrieved " + listing.toString());
                NewsListingCommand command = new NewsListingCommand();
                command.setId(listing.getId());
                command.setName(listing.getNewsDefinition().getName());
                command.setUrl(listing.getNewsDefinition().getParameters().get("url"));
                command.setSubscribeId(listing.getSubscribeId());
                command.setDisplayed(listing.isDisplayed());

                return command;
            } else {
                // otherwise, construct a brand new form

                // get user information
                Map userinfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
                String subscribeId = (String) userinfo.get(userToken);

                // create the form
                NewsListingCommand command = new NewsListingCommand();
                command.setSubscribeId(subscribeId);
                return command;
            }

        } else {
            // otherwise, construct a brand new form

            // get user information
            Map userinfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
            String subscribeId = (String) userinfo.get(userToken);

            // create the form
            NewsListingCommand command = new NewsListingCommand();
            command.setSubscribeId(subscribeId);
            return command;
        }
    }

    @Override
    protected void onSubmitAction(ActionRequest request,
                                  ActionResponse response, Object command, BindException errors)
            throws Exception {

        // get the form data
        NewsListingCommand form = (NewsListingCommand) command;

        // construct a news definition from the form data
        UserDefinedNewsConfiguration config = null;
        UserDefinedNewsDefinition definition = null;

        if (form.getId() > -1) {

            config = (UserDefinedNewsConfiguration) newsStore.getNewsConfiguration(form.getId());
            definition = config.getNewsDefinition();
            definition.addParameter("url", form.getUrl());
            definition.setName(form.getName());
            log.debug("Updating");

        } else {

            definition = new UserDefinedNewsDefinition();
            definition.setClassName(RomeAdapter.class.getName());
            definition.addParameter("url", form.getUrl());
            definition.setName(form.getName());
            newsStore.storeNewsDefinition(definition);

            config = new UserDefinedNewsConfiguration();
            config.setNewsDefinition(definition);
            config.setSubscribeId(form.getSubscribeId());
            config.setDisplayed(form.isDisplayed());
            log.debug("Insert new");
        }

        log.debug("User defined News configuration is \nUser: " + config.getSubscribeId());
        log.debug("User defined News definition is " + config.getNewsDefinition().getName());

        // save the news
        newsStore.storeNewsConfiguration(config);

        // send the user back to the main edit page
        response.setRenderParameter("action", "editPreferences");

    }

    private String userToken = "user.login.id";

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    private NewsStore newsStore;

    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

}
