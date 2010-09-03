package org.jasig.portlet.newsreader;

public class Preference {
	public static final String MAX_STORIES = "maxStories";
	public static final String SUMMARY_VIEW_STYLE = "summaryView";
	public static final String NEW_WINDOW = "newWindow";

	private String value;
	private Object options;
	private boolean readOnly;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getOptions() {
		return options;
	}

	public void setOptions(Object options) {
		this.options = options;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}
