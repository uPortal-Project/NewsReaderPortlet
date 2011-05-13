package org.jasig.portlet.newsreader.mvc.portlet.reader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("HELP")
public class HelpController {

    @RequestMapping
    public String getHelpView() {
        return "help";
    }
    
}
