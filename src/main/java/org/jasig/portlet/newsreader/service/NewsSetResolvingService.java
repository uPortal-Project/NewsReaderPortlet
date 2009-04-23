package org.jasig.portlet.newsreader.service;

import javax.portlet.ActionRequest;

import org.jasig.portlet.newsreader.NewsSet;

public interface NewsSetResolvingService {

	/**
	 * Returns a NewsSet based on the implemented algorithem.
	 * 
	 * Returned NewsSets are 'initalised' (loaded with pushed feeds).
	 * 
	 * The returned NewsSet is assocoiated with the calling portelt via a PortletPreference.
	 */
	public NewsSet getNewsSet(Long id, ActionRequest request);
	
	//public NewsSet createNewsSet(ActionRequest request, Set<String> roles);
	
	//public NewsSet getTemplateNewsSet(RenderRequest request, Set<String> roles);

}