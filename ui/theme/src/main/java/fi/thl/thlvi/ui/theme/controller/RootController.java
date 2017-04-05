package fi.thl.thlvi.ui.theme.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {
    
    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);
    
    @Value("${app.name}")
    private String name;
    
    @Value("${app.description}")
    private String description;
    
    @Value("${app.version}")
    private String version;
    
    @Value("${app.content.fragment}")
    private String appContentFragment;
    
    @RequestMapping("/")
    public String root(Model model) {
        
        model.addAttribute("name", name);
        model.addAttribute("description", description);
        model.addAttribute("version", version);
        
        model.addAttribute("content", appContentFragment);
        
        return "theme";
    }
}
