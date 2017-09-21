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
    Optional<UserDTO> user = convertToUserDTO(userHelper.getCurrentUser());
    return user.isPresent() ? user.get() : Collections.emptyMap();
  }

  private Optional<UserDTO> convertToUserDTO(Optional<UserWithProfile> optionalUser) {
    if (optionalUser.isPresent()) {
      UserWithProfile user = optionalUser.get();
      return Optional.of(new UserDTO(
        user.getUsername(),
        user.getUserProfile().getFirstName().orElse(""),
        user.getUserProfile().getLastName().orElse(""),
        user.getUserProfile().getEmail().orElse("")
      ));
    }
    else {
      return Optional.empty();
    }
  }

  @PostMapping(value = "/is-current-user-admin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public boolean isAdmin() {
    return userHelper.isCurrentUserAdmin();
  }

  @PostMapping(value = "/list-current-user-organizations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Collection<Organization> listCurrentUserOrganizations() {
    return userHelper.getCurrentUserOrganizations();
  }

  public static class UserDTO {

    private String username;
    private String fistName;
    private String lastName;
    private String email;

    public UserDTO(String username, String fistName, String lastName, String email) {
      this.username = username;
      this.fistName = fistName;
      this.lastName = lastName;
      this.email = email;
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

  }

}
