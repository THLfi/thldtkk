package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserHelper {

  public boolean isCurrentUserAdmin() {
    return getUserRoles().contains("AINEISTOEDITORI.FI_ADMIN");
  }

  private List<String> getUserRoles() {
    return SecurityContextHolder.getContext()
      .getAuthentication()
      .getAuthorities()
      .stream()
      .map(ga -> ga.getAuthority())
      .collect(Collectors.toList());
  }

  public Collection<Organization> getCurrentUserOrganizations() {
    Optional<UserWithProfile> user = getCurrentUser();
    return user.isPresent()
      ? user.get().getUserProfile().getOrganizations()
      : Collections.emptyList();
  }

  private Optional<UserWithProfile> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    else {
      return Optional.of((UserWithProfile) auth.getPrincipal());
    }
  }

}
