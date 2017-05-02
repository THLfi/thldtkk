package fi.thl.thldtkk.ui.editor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {
  @RequestMapping("/datasets/**")
  public String forwardToRoot() {
    return "forward:/";
  }
}
