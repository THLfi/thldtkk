package fi.thl.thldtkk.api.metadata.controller.v3;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Role;
import fi.thl.thldtkk.api.metadata.service.RoleService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/roles")
public class RoleController3 {

  @Autowired
  private RoleService roleService;

  @GetJsonMapping
  public List<Role> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return roleService.query(query).collect(toList());
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Role save(@RequestBody @Valid Role role) {
    return roleService.save(role);
  }

}
