package fi.thl.thldtkk.api.metadata.security;

import fi.thl.thldtkk.api.metadata.security.thlsso.ThlSsoRestAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
public class SecurityConfigurationThlSso {

  @Value("${sso.url:}")
  private String thlAuthenticationUrl;

  @Value("${sso.application:}")
  private String thlAuthenticationApplication;

  @Value("${sso.secretKey:}")
  private String thlAuthenticationSecretKey;

  @Bean
  public ThlSsoRestAuthenticationProvider thlSsoRestAuthenticationProvider() {
    return new ThlSsoRestAuthenticationProvider(
      thlAuthenticationUrl,
      thlAuthenticationApplication,
      thlAuthenticationSecretKey);
  }

}
