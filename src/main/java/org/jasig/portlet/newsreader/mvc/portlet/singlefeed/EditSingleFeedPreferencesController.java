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

package org.jasig.portlet.newsreader.mvc.portlet.singlefeed;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.Preference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

@Controller
@RequestMapping("EDIT")
public class EditSingleFeedPreferencesController implements InitializingBean {

	private List<Integer> optionsMaxStories;
	private Map<String,String> optionsViewTypes;
	
    protected final Log log = LogFactory.getLog(getClass());
		
    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String,String> m = new HashMap<String,String>();
        m.put("flyout", "a list with flyouts");
        m.put("titleAndAbstract", "a list of titles with summaries");
        m.put("title", "a list of titles only");
        optionsViewTypes = Collections.unmodifiableMap(m);
        
        optionsMaxStories = Arrays.asList(new Integer[] {5, 10, 15, 20}); 
        
    }

    @RequestMapping
	protected ModelAndView showForm(RenderRequest request, RenderResponse response) throws Exception {

        log.trace("handleRenderRequestInternal");
		
		//get the user preferences
		PortletPreferences preferences = request.getPreferences();
		Map<String, Preference> model = new HashMap<String, Preference>();
		
		Preference max = new Preference();
		max.setOptions(optionsMaxStories);
		max.setValue(preferences.getValue(Preference.MAX_STORIES, ""));
		max.setReadOnly(preferences.isReadOnly(Preference.MAX_STORIES));
		model.put("max", max);
		
		Preference view = new Preference();
		view.setOptions(optionsViewTypes);
		view.setValue(preferences.getValue(Preference.SUMMARY_VIEW_STYLE, ""));
		view.setReadOnly(preferences.isReadOnly(Preference.SUMMARY_VIEW_STYLE));
		model.put("view", view);
		
		Preference newWindow = new Preference();
		newWindow.setValue( preferences.getValue(Preference.NEW_WINDOW, Boolean.TRUE.toString()));
		newWindow.setReadOnly(preferences.isReadOnly(Preference.NEW_WINDOW));
		model.put("newWindow", newWindow);
		
		//build the model and view
		return new ModelAndView("editSingleFeed", model);

    }
    
    @RequestMapping(params = "action=saveDisplayPreference")
    public Map<Object, Object> savePreference(ActionRequest request,
            ActionResponse response) throws Exception {

        try {
            String prefName = request.getParameter("prefName");
            String prefValue = request.getParameter("prefValue");
            
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(prefName, prefValue);
            prefs.store();

            JSONObject json = new JSONObject();
            json.put("status", "success");

            Map<Object, Object> model = new HashMap<Object, Object>();
            model.put("json", json);
            return model;
        } catch (Exception e) {
            log.error("There was an error saving the preferences.", e);
            throw e;
        }

    }


}
