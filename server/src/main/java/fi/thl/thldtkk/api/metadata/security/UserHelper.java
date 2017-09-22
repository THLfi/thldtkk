package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserHelper {

  @Autowired
  private UserProfileService userProfileService;

  public UserProfile createUserProfile(String directoryUsername) {
    UserProfile newUserProfile = new UserProfile(UUID.randomUUID());
    newUserProfile.getExternalIds().add(directoryUsername);
    return userProfileService.save(newUserProfile);
  }

  public Optional<UserProfile> findExistingUserProfile(String directoryUsername) {
    return userProfileService.findAll()
      .stream()
      .filter(userProfile -> userProfile.getExternalIds().contains(directoryUsername))
      .findFirst();
}

  public String getDirectoryUsername(String username, UserDirectory userDirectory) {
    if (userDirectory != null && !userDirectory.toString().contains("/")) {
       return userDirectory.toString() + "/" + username;
    }
    return username;
  }

  public boolean isCurrentUserLoggedIn() {
    return SecurityContextHolder.getContext().getAuthentication() != null ? true : false;
  }

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

  public Optional<UserWithProfile> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    else {
      return Optional.of((UserWithProfile) auth.getPrincipal());
    }
  }

  public UserDetails loadUserByUsername(String username, UserDirectory userDirectory,
       UserDetails details)
       throws UsernameNotFoundException {
    String directoryUsername = getDirectoryUsername(username, userDirectory);
    Optional<UserProfile> existingUserProfile = findExistingUserProfile(
           directoryUsername);
    UserProfile userProfile;
    if (existingUserProfile.isPresent()) {
      userProfile = existingUserProfile.get();
    } else {
      userProfile = createUserProfile(directoryUsername);
    }
    return new UserWithProfile(directoryUsername, details, userProfile);
  }

}
