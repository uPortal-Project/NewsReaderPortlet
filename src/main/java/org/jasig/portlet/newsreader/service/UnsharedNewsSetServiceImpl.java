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
	
	public NewsSet getNewsSet(Long id, PortletRequest request) {

		if (id < 0) {
			// if the id has not yet been set in the session, return a new NewsSet
			PortletSession session = request.getPortletSession();
			Set<String> roles = (Set<String>) session.getAttribute("userRoles", PortletSession.PORTLET_SCOPE);
			if (request instanceof ActionRequest) {
				// if this is an action request, go ahead and create a new
				// set and store it in the database
				log.debug("creating and saving new set");
				NewsSet set = createNewsSet((ActionRequest) request, roles);
				return set;
			} else {
				// if this is a render request, we can't set portlet preferences
				// just return a template news set to use until we can
				// access the portlet preferences
				log.debug("creating new template set");
				return getTemplateNewsSet((RenderRequest) request, roles);
			}
		} else {
			log.debug("retrieving news set " + id);
			return newsStore.getNewsSet(id);
		}
	}
	
	public NewsSet createNewsSet(ActionRequest request, Set<String> roles) {
		NewsSet set = new NewsSet();
		set.setUserId(request.getRemoteUser());
		newsStore.initNews(set, roles);
		newsStore.storeNewsSet(set);
		PortletSession session = request.getPortletSession();
		session.setAttribute("setId", set.getId(), PortletSession.PORTLET_SCOPE);
		PortletPreferences preferences = request.getPreferences();
		try {
			preferences.setValue("newsSetId", String.valueOf(set.getId()));
			preferences.store();
		} catch (ReadOnlyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return set;
	}
	
	public NewsSet getTemplateNewsSet(RenderRequest request, Set<String> roles) {
		NewsSet set = new NewsSet();
		set.setUserId(request.getRemoteUser());
		newsStore.initNews(set, roles);
		return set;
	}

	private NewsStore newsStore;
	public void setNewsStore(NewsStore newsStore) {
		this.newsStore = newsStore;
	}
	
}
