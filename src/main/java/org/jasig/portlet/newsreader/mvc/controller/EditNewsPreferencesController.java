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
import org.jasig.portlet.newsreader.PredefinedNewsConfiguration;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.UserDefinedNewsConfiguration;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import javax.portlet.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * EditNewsPreferencesController provides the main edit page for the News Reader
 * portlet.  The page allows users to view, add, delete and edit all available
 * feeds.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
public class EditNewsPreferencesController extends AbstractController {

    private static Log log = LogFactory.getLog(EditNewsPreferencesController.class);

    @Override
    public ModelAndView handleRenderRequestInternal(RenderRequest request,
                                                    RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();

        // get user information
        Map userinfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
        String subscribeId = (String) userinfo.get(userToken);

        // add the user-defined newss to the model
        List<UserDefinedNewsConfiguration> myNewsConfigurations = newsStore.getUserDefinedNewsConfigurations(subscribeId, false);
        model.put("myNewsConfigurations", myNewsConfigurations);

        // add the predefined newss to the model
        List<PredefinedNewsConfiguration> predefinedNewsConfigurations = newsStore.getPredefinedNewsConfigurations(subscribeId, false);
        model.put("predefinedNewsConfigurations", predefinedNewsConfigurations);

        // get the user's role listings
        PortletSession session = request.getPortletSession();
        Set<String> userRoles = (Set<String>) session.getAttribute("userRoles");

        // get a list of predefined newss the user doesn't
        // currently have configured
        List<PredefinedNewsDefinition> definitions = newsStore.getHiddenPredefinedNewsDefinitions(subscribeId, userRoles);
        model.put("hiddenFeeds", definitions);

        model.put("predefinedEditActions", predefinedEditActions);

        // return the edit view
        return new ModelAndView("/editNews", "model", model);
    }

    @Override
    protected void handleActionRequestInternal(ActionRequest request,
                                               ActionResponse response) throws Exception {
        Long id = Long.parseLong(request.getParameter("id"));
        String actionCode = request.getParameter("actionCode");
        PortletSession session = request.getPortletSession();

        if (actionCode.equals("delete")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            newsStore.deleteNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("show")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(true);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("hide")) {
            NewsConfiguration config = newsStore.getNewsConfiguration(id);
            config.setDisplayed(false);
            newsStore.storeNewsConfiguration(config);
            //Map<Long, String> hidden = (Map<Long, String>) session.getAttribute("hiddenNewss");
            //hidden.remove(config.getId());
        } else if (actionCode.equals("showNew")) {
            // get user information
            Map userinfo = (Map) request.getAttribute(PortletRequest.USER_INFO);
            String subscribeId = (String) userinfo.get(userToken);
            PredefinedNewsDefinition definition = (PredefinedNewsDefinition) newsStore.getNewsDefinition(id);
            log.debug("definition to save " + definition.toString());
            PredefinedNewsConfiguration config = new PredefinedNewsConfiguration();
            config.setSubscribeId(subscribeId);
            config.setNewsDefinition(definition);
            newsStore.storeNewsConfiguration(config);
        }
    }


    private String userToken = "user.login.id";

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }


    private Map predefinedEditActions;

    public void setPredefinedEditActions(Map predefinedEditActions) {
        this.predefinedEditActions = predefinedEditActions;
    }

    private NewsStore newsStore;

    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

}
