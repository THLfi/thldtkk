package fi.thl.thldtkk.ui.catalog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {
  @RequestMapping("/datasets/**")
  public String forwardToRoot() {
    return "forward:/";
  }
}
