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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.PredefinedNewsDefinition;
import org.jasig.portlet.newsreader.dao.NewsStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;


/**
 * AdminNewsController provides a main administrative view for the news
 * portlet.  The page is available to users in the configured "newsAdmin"
 * role.
 *
 * @author Anthony Colebourne
 * @author Jen Bourey
 */
@Controller
@RequestMapping("EDIT")
public class AdminNewsController {

    protected final Log log = LogFactory.getLog(getClass());

    private NewsStore newsStore;

    @Autowired(required = true)
    public void setNewsStore(NewsStore newsStore) {
        this.newsStore = newsStore;
    }

    @RenderMapping(params="action=administration")

    public ModelAndView getAdminView(RenderRequest request,RenderResponse response) {
    	
    	log.debug("Entering news admin");


        Map<String, Object> model = new HashMap<String, Object>();

        // get a list of all predefined newss
        model.put("feeds", newsStore.getPredefinedNewsConfigurations());
        return new ModelAndView("/adminNews", "model", model);

    }

    @ActionMapping(params="action=deletePredefinedFeed")
    public void deleteFeed(@RequestParam("id") Long id) {
        PredefinedNewsDefinition def = newsStore.getPredefinedNewsDefinition(id);
        newsStore.deleteNewsDefinition(def);
    }

}
