/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.newsreader.mvc;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Creating an abstract superclass for news in VIEW mode as a first step toward 
 * combining controllers for single-feed and regular.  I believe we'd be better 
 * served -- have less chance for mismatch and regression -- if the 2 approaches 
 * used the same controllers, and the differences in behavior were factored out 
 * into different subclasses of service objects.  We could then configure these 
 * services appropriately in each portlet's Spring sub-context.
 * 
 * So I suggest factoring common code out of the subclasses (and into here) 
 * bit-by-bit until the controllers are no different, at which point we can 
 * loose this class if it seems like a good idea then.
 * 
 * @author awills
 */
public class AbstractNewsController {

    @ModelAttribute("isAdmin")
    public boolean isAdmin(PortletRequest req) {
        boolean rslt = false;  // default...
        final PortletSession session = req.getPortletSession(false);
        if (session != null) {
            Boolean attr = (Boolean) session.getAttribute("isAdmin", PortletSession.PORTLET_SCOPE);
            rslt = attr != null
                    ? attr
                    : false;
        }
        return rslt;
    }    

    @ModelAttribute("isGuest")
    public boolean isGuest(PortletRequest req) {
        return req.getRemoteUser() == null;
    }    

    @ModelAttribute("supportsEdit")
    public boolean supportsEdit(PortletRequest req) {
        return req.isPortletModeAllowed(PortletMode.EDIT);
    }    

    @ModelAttribute("supportsHelp")
    public boolean supportsHelp(PortletRequest req) {
        return req.isPortletModeAllowed(PortletMode.HELP);
    }    

}
