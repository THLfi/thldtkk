package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.SystemRole;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import fi.thl.thldtkk.api.metadata.service.EditorSystemRoleService;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/systemRoles")

public class EditorSystemRoleController {

  @Autowired
  private EditorSystemRoleService systemRoleService;

  @GetJsonMapping
  public List<SystemRole> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return systemRoleService.find(query, -1);
  }

}
