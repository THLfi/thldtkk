package fi.thl.thldtkk.ui.root.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redirect request to given URL.
 */
@Controller
public class RootController {

    private static final Logger LOG = LoggerFactory.getLogger(RootController.class);

    @Value("${root.redirect.url:/error}")
    private String redirectUrl;

    @RequestMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        LOG.debug("Redirect to {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
