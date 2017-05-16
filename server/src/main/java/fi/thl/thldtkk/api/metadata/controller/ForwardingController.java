package fi.thl.thldtkk.api.metadata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardingController {

  @RequestMapping({"/editor/**", "/catalog/**"})
  public String forwardToRoot() {
    return "forward:/";
  }

}
