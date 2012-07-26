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

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.Preference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

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

	@RenderMapping
	protected ModelAndView showForm(RenderRequest request, RenderResponse response) throws Exception {

        log.trace("handleRenderRequestInternal");
		
		//get the user preferences
		PortletPreferences preferences = request.getPreferences();
		Map<String, Preference> model = new HashMap<String, Preference>();

		log.debug("name preference readOnly: "+preferences.isReadOnly(Preference.FFED_NAME));
		Preference name = new Preference();
		name.setValue(preferences.getValue(Preference.FFED_NAME, ""));
		name.setReadOnly(preferences.isReadOnly(Preference.FFED_NAME));
		model.put("name", name);

		log.debug("url preference readOnly: "+preferences.isReadOnly(Preference.FEED_URL));
		Preference url = new Preference();
		url.setValue(preferences.getValue(Preference.FEED_URL, ""));
		url.setReadOnly(preferences.isReadOnly(Preference.FEED_URL));
		model.put("url", url);

		log.debug("className preference readOnly: "+preferences.isReadOnly(Preference.CLASS_NAME));
		Preference className = new Preference();
		className.setValue(preferences.getValue(Preference.CLASS_NAME, ""));
		className.setReadOnly(preferences.isReadOnly(Preference.CLASS_NAME));
		model.put("className", className);

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
    
	@ResourceMapping
    public String savePreference(ResourceRequest request,
    		ResourceResponse response, Model model) throws Exception {

        try {
            String prefName = request.getParameter("prefName");
            String prefValue = request.getParameter("prefValue");
            
            log.debug("Storing: "+prefName+" : "+prefValue);
            
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(prefName, prefValue);
            prefs.store();

            JSONObject json = new JSONObject();
            json.put("status", "success");

            model.addAttribute("json", json);
            return "json";
        } catch (Exception e) {
            log.error("There was an error saving the preferences.", e);
            throw e;
        }

    }


}
