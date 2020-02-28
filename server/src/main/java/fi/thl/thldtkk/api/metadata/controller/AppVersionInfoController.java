package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.AppVersionInfo;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/version")
public class AppVersionInfoController {

  @Autowired
  private AppVersionInfo appVersionInfo;

  @GetJsonMapping
  public AppVersionInfo getVersion() {
    return appVersionInfo;
  }

}
