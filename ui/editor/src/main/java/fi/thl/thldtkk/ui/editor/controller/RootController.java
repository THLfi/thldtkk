package fi.thl.thldtkk.ui.editor.controller;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class RootController {

    @Autowired
    private ServletContext context;

    @Value("${app.name}")
    private String name;

    @Value("${app.description}")
    private String description;

    @Value("${app.version}")
    private String version;

    @Value("${app.root.template}")
    private String rootTemplate;

    @Value("${app.content.fragment}")
    private String appContentFragment;

    @Value("${app.css.files}")
    private String appCssFiles;

    @Value("${app.js.files}")
    private String appJsFiles;

    @Value("${metadata.service.url}")
    private String metadataServiceUrl;

    @Value("${catalog.ui.url}")
    private String catalogUiUrl;

    @Value("${editor.ui.url}")
    private String editorUiUrl;

    @Value("${main.ui.url}")
    private String mainUiUrl;

    @RequestMapping("/")
    public String index(Model model) {

        model.addAttribute("name", name);
        model.addAttribute("description", description);
        model.addAttribute("version", version);

        model.addAttribute("content", appContentFragment);
        model.addAttribute("appCssFiles", appCssFiles.split(","));
        model.addAttribute("appJsFiles", appJsFiles.split(","));

        model.addAttribute("metadataServiceUrl", metadataServiceUrl);
        model.addAttribute("catalogUiUrl", catalogUiUrl);
        model.addAttribute("editorUiUrl", editorUiUrl);

        model.addAttribute("contextPath", context.getContextPath());

        model.addAttribute("mainUiUrl", mainUiUrl);

        return rootTemplate;
    }
}
