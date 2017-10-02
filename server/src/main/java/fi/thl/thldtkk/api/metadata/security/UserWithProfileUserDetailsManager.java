package fi.thl.thldtkk.api.metadata.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class UserWithProfileUserDetailsManager implements UserDetailsService {

  private final UserProfileHelper userProfileHelper;
  private final UserDirectory userDirectory;
  private final UserDetailsManager delegate;

  public UserWithProfileUserDetailsManager(UserProfileHelper userProfileHelper, UserDirectory userDirectory,
                                           UserDetailsManager delegate) {
    this.userProfileHelper = userProfileHelper;
    this.userDirectory = userDirectory;
    this.delegate = delegate;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails user = delegate.loadUserByUsername(username);
    return userProfileHelper.convertToUserWithProfile(username, this.userDirectory, user);
  }

}
