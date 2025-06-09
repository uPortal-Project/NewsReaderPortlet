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
package org.jasig.portlet.newsreader.mvc.portlet.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.jasig.portlet.newsreader.mvc.AbstractNewsController;
import org.jasig.portlet.newsreader.service.RolesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;
import org.springframework.web.portlet.util.PortletUtils;


/**
 * AdminNewsController provides a main administrative view for the news portlet.  The page is available
 * to users in the configured <code>NEWS_ADMIN_ROLE</code> role.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 * @since 5.1.1
 */
@Controller
@RequestMapping("EDIT")
public class AdminNewsController {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = true)
    private NewsStore newsStore;

    /**
     * <p>getAdminView.</p>
     *
     * @param request a {@link javax.portlet.RenderRequest} object
     * @return a {@link org.springframework.web.portlet.ModelAndView} object
     * @throws javax.portlet.PortletSecurityException if any.
     */
    @RenderMapping(params="action=administration")
    public ModelAndView getAdminView(RenderRequest request) throws PortletSecurityException {
        if (!request.isUserInRole(AbstractNewsController.NEWS_ADMIN_ROLE)) {
            log.warn("User [ {} ] with IP [ {} ] tried to access news administration!",
                    request.getRemoteUser(),
                    request.getProperty("REMOTE_ADDR"));
            throw new PortletSecurityException("User does not have required admin role");
        }

        log.debug("Entering news admin");

        Map<String, Object> model = new HashMap<String, Object>();

        // get a list of all predefined newss
        model.put("feeds", newsStore.getPredefinedNewsConfigurations());
        return new ModelAndView("adminNews", "model", model);

    }

    /**
     * <p>deleteFeed.</p>
     *
     * @param id a {@link java.lang.Long} object
     * @param request a {@link javax.portlet.ActionRequest} object
     * @throws javax.portlet.PortletSecurityException if any.
     */
    @ActionMapping(params="action=deletePredefinedFeed")
    public void deleteFeed(@RequestParam("id") Long id, ActionRequest request) throws PortletSecurityException {
        if (!request.isUserInRole(AbstractNewsController.NEWS_ADMIN_ROLE)) {
            log.warn("User [ {} ] with IP [ {} ] tried to access news administration!",
                    request.getRemoteUser(),
                    request.getProperty("REMOTE_ADDR"));
            throw new PortletSecurityException("User does not have required admin role");
        }

        PredefinedNewsDefinition def = newsStore.getPredefinedNewsDefinition(id);
        newsStore.deleteNewsDefinition(def);
    }

}
