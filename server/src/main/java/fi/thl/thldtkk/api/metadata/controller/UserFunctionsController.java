package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/v2/user-functions")
public class UserFunctionsController {

  private final UserHelper userHelper;

  @Autowired
  public UserFunctionsController(UserHelper userHelper) {
    this.userHelper = userHelper;
  }

  @PostMapping(value = "/is-current-user-admin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public boolean isAdmin() {
    return userHelper.isCurrentUserAdmin();
  }

  @PostMapping(value = "/list-current-user-organizations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Collection<Organization> listCurrentUserOrganizations() {
    return userHelper.getCurrentUserOrganizations();
  }

}
