package fi.thl.thldtkk.api.metadata.util.spring.security;

import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ThlSsoUserDetails implements UserDetails {

    private String password;
    private String username;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public ThlSsoUserDetails(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getCredentials() != null) {
                this.password = authentication.getCredentials().toString();
            }
            this.username = authentication.getName();
            this.enabled = authentication.isAuthenticated();
            this.authorities = authentication.getAuthorities();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
