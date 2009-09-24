package org.jasig.portlet.newsreader.mvc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.newsreader.service.IInitializationService;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.ParameterizableViewController;

public class SingleFeedNewsController extends ParameterizableViewController {

    private static Log log = LogFactory.getLog(SingleFeedNewsController.class);

    public ModelAndView handleRenderRequestInternal(RenderRequest request,
                                                    RenderResponse response) throws Exception {

        Map<String, Object> model = new HashMap<String, Object>();
        PortletSession session = request.getPortletSession(true);

        /**
         * If this is a new session, perform any necessary
         * portlet initialization.
         */
        if (session.getAttribute("initialized") == null) {

            // perform any other configured initialization tasks
            for (IInitializationService service : initializationServices) {
                service.initialize(request);
            }

            // mark this session as initialized
            session.setAttribute("initialized", "true");
            session.setMaxInactiveInterval(60 * 60 * 2);

        } 

        model.put("isAdmin", (Boolean) session.getAttribute("isAdmin", PortletSession.PORTLET_SCOPE));
        
        log.debug("forwarding to " + getViewName());
        return new ModelAndView(getViewName(), "model", model);
    }

	
    private List<IInitializationService> initializationServices;
    public void setInitializationServices(List<IInitializationService> services) {
        this.initializationServices = services;
    }

}
