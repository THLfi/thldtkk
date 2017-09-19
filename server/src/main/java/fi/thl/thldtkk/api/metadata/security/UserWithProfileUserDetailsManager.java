package fi.thl.thldtkk.api.metadata.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

public class UserWithProfileUserDetailsManager implements UserDetailsService {

  @Autowired
  private UserHelper userHelper;

  private final UserDirectory userDirectory;
  private final UserDetailsManager delegate;

  public UserWithProfileUserDetailsManager(UserDirectory userDirectory,
                                           UserDetailsManager delegate) {
    this.userDirectory = userDirectory;
    this.delegate = delegate;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetails user = delegate.loadUserByUsername(username);

    return userHelper.loadUserByUsername(username, this.userDirectory, user);
  }

}
