package org.jasig.portlet.newsreader.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;

import net.sf.json.JSONObject;

import org.jasig.web.portlet.mvc.AbstractAjaxController;

public class SaveDisplayPreferenceController extends AbstractAjaxController {
	
	private Map<String, List<String>> allowedValues;
	
	@Override
	protected Map<Object, Object> handleAjaxRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
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
	}

}
