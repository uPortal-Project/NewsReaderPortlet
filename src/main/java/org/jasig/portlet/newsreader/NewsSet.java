package org.jasig.portlet.newsreader;

import java.util.HashSet;
import java.util.Set;

public class NewsSet {

	private Long id = new Long(-1);
	private String name;
	private Set<NewsConfiguration> newsConfigurations = new HashSet<NewsConfiguration>();
	private String userId;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<NewsConfiguration> getNewsConfigurations() {
		return newsConfigurations;
	}
	public void setNewsConfigurations(Set<NewsConfiguration> calendars) {
		this.newsConfigurations = calendars;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void addNewsConfiguration(NewsConfiguration config) {
		config.setNewsSet(this);
		this.newsConfigurations.add(config);
	}
	
	
}
