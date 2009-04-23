package org.jasig.portlet.newsreader.service;

import java.io.IOException;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.NewsSet;
import org.jasig.portlet.newsreader.dao.NewsStore;

public class UnsharedNewsSetServiceImpl implements NewsSetResolvingService {
	
    private static Log log = LogFactory.getLog(UnsharedNewsSetServiceImpl.class);
	
	public NewsSet getNewsSet(Long id, ActionRequest request) {
		NewsSet set = null;
		
		PortletSession session = request.getPortletSession();
		
		if (id < 0) { // No preference set, need to find a set or create a new one
			log.debug("Creating and saving new set.");
			set = createNewsSet(request);
		} else { 
			// preference already set, just fetch this news
			log.debug("Retrieving news set " + id);
			set = newsStore.getNewsSet(id);
		}

		// Persistant set is now loaded but may still need re-initalising since last use.
		// by adding setId to session, we signal that initaisation has taken place.
		
		if (session.getAttribute("setId") == null) {
			Set<String> roles = (Set<String>) session.getAttribute("userRoles", PortletSession.PORTLET_SCOPE);
			
			newsStore.initNews(set, roles);
			newsStore.storeNewsSet(set);
			session.setAttribute("setId", set.getId(), PortletSession.PORTLET_SCOPE);
		}
		
		return set;
	}
	
	private NewsSet createNewsSet(ActionRequest request) {
		NewsSet set = new NewsSet();
		set.setUserId(request.getRemoteUser());
		newsStore.storeNewsSet(set);
		return set;
	}
	
	private NewsStore newsStore;
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}
	
}
