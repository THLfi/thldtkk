package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/auth/roles")
public class UserRoleController {

  @GetJsonMapping
  public List<String> getUserRoles() {
    return SecurityContextHolder.getContext()
      .getAuthentication()
      .getAuthorities()
      .stream()
      .map(ga -> ga.getAuthority())
      .collect(Collectors.toList());
  }

}
