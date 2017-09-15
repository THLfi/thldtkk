package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.service.v3.UserProfileService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.Optional;
import java.util.UUID;

public class UserWithProfileUserDetailsManager implements UserDetailsService {

  private final UserDirectory userDirectory;
  private final UserDetailsManager delegate;
  private final UserProfileService userProfileService;

  public UserWithProfileUserDetailsManager(UserDirectory userDirectory,
                                           UserDetailsManager delegate,
                                           UserProfileService userProfileService) {
    this.userDirectory = userDirectory;
    this.delegate = delegate;
    this.userProfileService = userProfileService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails user = delegate.loadUserByUsername(username);

    String directoryUsername = getDirectoryUsername(username);

    Optional<UserProfile> existingUserProfile = findExistingUserProfile(directoryUsername);
    UserProfile userProfile;

    if (existingUserProfile.isPresent()) {
      userProfile = existingUserProfile.get();
    }
    else {
      userProfile = createUserProfile(directoryUsername);
    }

    return new UserWithProfile(directoryUsername, user, userProfile);
  }

  private String getDirectoryUsername(String username) {
    return userDirectory.toString() + "/" + username;
  }

  private Optional<UserProfile> findExistingUserProfile(String directoryUsername) {
    return userProfileService.findAll()
      .stream()
      .filter(userProfile -> userProfile.getExternalIds().contains(directoryUsername))
      .findFirst();
  }

  private UserProfile createUserProfile(String directoryUsername) {
    UserProfile newUserProfile = new UserProfile(UUID.randomUUID());
    newUserProfile.getExternalIds().add(directoryUsername);
    return userProfileService.save(newUserProfile);
  }

}
