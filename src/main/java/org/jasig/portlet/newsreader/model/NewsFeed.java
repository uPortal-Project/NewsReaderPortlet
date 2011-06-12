package org.jasig.portlet.newsreader.model;

import java.io.Serializable;
import java.util.List;

public class NewsFeed implements Serializable {

    private List<NewsFeedItem> entries;
    private String author;
    private String link;
    private String title;
    private String copyright;

    public List<NewsFeedItem> getEntries() {
        return entries;
    }

    public void setEntries(List<NewsFeedItem> entries) {
        this.entries = entries;
    }
    
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }
    
}
