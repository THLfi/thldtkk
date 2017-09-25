package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.security.UserWithProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v3/user-functions")
public class UserFunctionsController {

  private final UserHelper userHelper;

  @Autowired
  public UserFunctionsController(UserHelper userHelper) {
    this.userHelper = userHelper;
  }

  @GetMapping(value = "/get-current-user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object getCurrentUser() {
    Optional<UserDTO> user = getCurrentUserDTO();
    return user.isPresent() ? user.get() : Collections.emptyMap();
  }

  private Optional<UserDTO> getCurrentUserDTO() {
    Optional<UserWithProfile> currentUser = userHelper.getCurrentUser();
    if (currentUser.isPresent()) {
      UserWithProfile user = currentUser.get();
      return Optional.of(new UserDTO(
        user.getUsername(),
        user.getUserProfile().getFirstName().orElse(""),
        user.getUserProfile().getLastName().orElse(""),
        user.getUserProfile().getEmail().orElse(""),
        userHelper.isCurrentUserLoggedIn(),
        userHelper.isCurrentUserAdmin()));
    }
    else {
      return Optional.empty();
    }
  }

  @GetMapping(value = "/list-current-user-roles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<String> listCurrentUserRoles() {
    return userHelper.getUserRoles();
  }

  @PostMapping(value = "/list-current-user-organizations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Collection<Organization> listCurrentUserOrganizations() {
    return userHelper.getCurrentUserOrganizations();
  }

  public static class UserDTO {

    private final String username;
    private final String fistName;
    private final String lastName;
    private final String email;
    private final boolean isLoggedIn;
    private final boolean isAdmin;

    public UserDTO(String username, String fistName, String lastName, String email, boolean isLoggedIn, boolean isAdmin) {
      this.username = username;
      this.fistName = fistName;
      this.lastName = lastName;
      this.email = email;
      this.isLoggedIn = isLoggedIn;
      this.isAdmin = isAdmin;
    }

    public String getUsername() {
      return username;
    }

    public String getFistName() {
      return fistName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getEmail() {
      return email;
    }

    public boolean isLoggedIn() {
      return isLoggedIn;
    }

    public boolean isAdmin() {
      return isAdmin;
    }

  }

}
