package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.security.UserWithProfile;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/user")
public class UserController {

  @GetJsonMapping
  public String getUsername() {
    Optional<UserWithProfile> user = getUser();
    if (!user.isPresent()) {
      return "";
    }
    else {
      return user.get().getUsername();
    }
  }

  private Optional<UserWithProfile> getUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    else {
      return Optional.of((UserWithProfile) auth.getPrincipal());
    }
  }

  @GetJsonMapping("/roles")
  public List<String> getUserRoles() {
    return SecurityContextHolder.getContext()
      .getAuthentication()
      .getAuthorities()
      .stream()
      .map(ga -> ga.getAuthority())
      .collect(Collectors.toList());
  }

  @GetJsonMapping("/organizations")
  public Collection<Organization> getUserOrganizations() {
    Optional<UserWithProfile> user = getUser();
    return user.isPresent()
      ? user.get().getUserProfile().getOrganizations()
      : Collections.emptyList();
  }

}
