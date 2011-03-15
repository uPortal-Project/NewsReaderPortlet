package org.jasig.portlet.newsreader.model;

import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Revision$
 */
public class NewsFeedItem extends SyndEntryImpl {

    private static final long serialVersionUID = 9169435530958004414L;
    
    private String imageUrl;
    
    public NewsFeedItem() {
        super();
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String url) {
        this.imageUrl = url;
    }
    
}
