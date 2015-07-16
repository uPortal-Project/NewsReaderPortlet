package org.jasig.portlet.newsreader.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultFullStory implements FullStory {
	 
	protected final Log log = LogFactory.getLog(getClass());
	private String content;

	public DefaultFullStory(String content) {
		super();
		this.content = content;
	}

	@Override
	public String getFullStory() {
		return content;
	}
}
