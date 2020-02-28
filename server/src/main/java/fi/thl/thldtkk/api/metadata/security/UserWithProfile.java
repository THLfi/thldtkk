package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserWithProfile implements UserDetails {

  private final String directoryUsername;
  private final UserDetails delegate;
  private final UserProfile userProfile;
  private final Set<? extends GrantedAuthority> authorities;

  public UserWithProfile(String directoryUsername, UserDetails delegate, UserProfile userProfile) {
    this.directoryUsername = directoryUsername;
    this.delegate = delegate;
    this.userProfile = userProfile;
    authorities = initAuthorities(delegate, userProfile);
  }

  private Set<GrantedAuthority> initAuthorities(UserDetails delegate, UserProfile userProfile) {
    Set<GrantedAuthority> authorities = new LinkedHashSet<>();

    delegate.getAuthorities()
      .stream()
      .map(originalAuthority -> new SimpleGrantedAuthority(originalAuthority.getAuthority()))
      .forEach(simpleAuthority -> authorities.add(simpleAuthority));

    userProfile.getRoles()
      .stream()
      .map(role -> new SimpleGrantedAuthority(role))
      .forEach(simpleAuthority -> authorities.add(simpleAuthority));

    return Collections.unmodifiableSet(authorities);
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return delegate.getPassword();
  }

  @Override
  public String getUsername() {
    return directoryUsername;
  }

  @Override
  public boolean isAccountNonExpired() {
    return delegate.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return delegate.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return delegate.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return delegate.isEnabled();
  }

  @Override
  public String toString() {
    return getUsername();
  }

}
