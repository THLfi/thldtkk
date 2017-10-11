package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserProfileHelper {

  private final UserProfileService userProfileService;

  @Autowired
  public UserProfileHelper(UserProfileService userProfileService) {
    this.userProfileService = userProfileService;
  }

  public UserWithProfile convertToUserWithProfile(String username,
                                                  UserDirectory userDirectory,
                                                  UserDetails delegateUserDetails) {
    String directoryUsername = userDirectory.getDirectoryUsername(username);

    Optional<UserProfile> existingUserProfile = userProfileService.getByExternalId(directoryUsername);

    UserProfile userProfile;
    if (existingUserProfile.isPresent()) {
      userProfile = existingUserProfile.get();
    } else {
      userProfile = createUserProfile(directoryUsername);
    }

    return new UserWithProfile(directoryUsername, delegateUserDetails, userProfile);
  }

  private UserProfile createUserProfile(String directoryUsername) {
    UserProfile newUserProfile = new UserProfile(UUID.randomUUID());
    newUserProfile.getExternalIds().add(directoryUsername);
    newUserProfile.getRoles().add(UserRoles.USER);
    return userProfileService.save(newUserProfile);
  }

}
