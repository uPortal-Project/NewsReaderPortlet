package org.jasig.portlet.newsreader.service;

import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.jasig.portlet.newsreader.NewsSet;

public interface NewsSetResolvingService {

	public NewsSet getNewsSet(Long id, PortletRequest request);
	
	public NewsSet createNewsSet(ActionRequest request, Set<String> roles);
	
	public NewsSet getTemplateNewsSet(RenderRequest request, Set<String> roles);

}