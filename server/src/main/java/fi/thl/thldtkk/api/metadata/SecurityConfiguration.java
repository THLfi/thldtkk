package fi.thl.thldtkk.api.metadata;

import fi.thl.thldtkk.api.metadata.security.UserDirectory;
import fi.thl.thldtkk.api.metadata.security.UserWithProfileUserDetailsManager;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

  @Value("${users.properties.resource}")
  private Resource usersPropertiesResource;

  @Autowired
  private UserProfileService userProfileService;

  @Bean
  public UserDetailsService propertiesBasedUserDetailsService() throws IOException {
    Properties properties = PropertiesLoaderUtils.loadProperties(
      new EncodedResource(usersPropertiesResource, "UTF-8"));

    LOG.info("Loaded {} users from {}", properties.size(), usersPropertiesResource);

    return new UserWithProfileUserDetailsManager(
      UserDirectory.LOCAL,
      new InMemoryUserDetailsManager(properties),
      userProfileService);
  }

  @Autowired
  public void configureAuthenticationManager(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.userDetailsService(propertiesBasedUserDetailsService());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
        // Set "XSRF-TOKEN" cookie which Angular's HTTP components can use out-of-the-box. For more info, see:
        // https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
        // https://angular.io/guide/http#security-xsrf-protection
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
      .authorizeRequests()
        .antMatchers(HttpMethod.POST).authenticated()
        .antMatchers(HttpMethod.PUT).authenticated()
        .antMatchers(HttpMethod.DELETE).authenticated()
        .antMatchers("/editor/**").authenticated()
        .anyRequest().permitAll()
/*
        // "Forbidden by default" matchers that should be used when we have public API available for all entities.
        .antMatchers(
          "/",
          "/api/v2/public/**",
          "/catalog/**").permitAll()
        .anyRequest().authenticated()
*/
        // Static assets such as JS, CSS and fonts are allowed by default
        .and()
      .httpBasic()
        .realmName("www.aineistoeditori.fi")
        .and()
      .logout()
        .permitAll();
  }

}
