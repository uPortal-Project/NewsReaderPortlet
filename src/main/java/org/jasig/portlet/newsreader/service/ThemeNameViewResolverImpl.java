package org.jasig.portlet.newsreader.service;

import javax.portlet.PortletRequest;

/**
 * ThemeNameViewResolver determines appropriate views by examining a "themeName"
 * portlet request property and comparing it to known mobile theme names.  This
 * implementation allows the portlet to delegate user agent inspection to the 
 * portal and also accounts for a potential user choice to use a portal version 
 * that does not match the automatic assignment.
 * 
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Revision$
 */
public class ThemeNameViewResolverImpl implements IViewResolver {

    protected static final String THEME_NAME_PROPERTY = "themeName";
    protected static final String MOBILE_THEMES_KEY = "mobileThemes";
    protected static final String[] MOBILE_THEMES_DEFAULT = new String[]{ "UniversalityMobile" };
    
    public String getSingleFeedView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewSingleFeed-jQM";
        } else {
            return "viewSingleFeed";
        }
    }

    public String getReaderView(PortletRequest request) {
        if (isMobile(request)) {
            return "viewNews-jQM";
        } else {
            return "viewNews";
        }
    }
    
    public String getPreferencesView(PortletRequest request) {
        if (isMobile(request)) {
            return "editNews-jQM";
        } else {
            return "editNews";
        }
    }
    
    protected boolean isMobile(PortletRequest request) {
        String[] mobileThemes = request.getPreferences().getValues(MOBILE_THEMES_KEY, MOBILE_THEMES_DEFAULT);
        String themeName = request.getProperty(THEME_NAME_PROPERTY);
        if (themeName == null) {
            return false;
        }
        
        for (String theme : mobileThemes) {
            if (themeName.equals(theme)) {
                return true;
            }
        }
        
        return false;
    }
    
}
