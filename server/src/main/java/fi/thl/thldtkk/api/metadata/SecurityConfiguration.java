package fi.thl.thldtkk.api.metadata;

import fi.thl.thldtkk.api.metadata.security.UserDirectory;
import fi.thl.thldtkk.api.metadata.security.UserWithProfileUserDetailsManager;
import fi.thl.thldtkk.api.metadata.util.spring.security.ThlSsoRestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);

  @Value("${users.properties.resource}")
  private Resource usersPropertiesResource;

  @Value("${sso.url}")
  private String thlAuthenticationUrl;

  @Value("${sso.application}")
  private String thlAuthenticationApplication;

  @Value("${sso.secretKey}")
  private String thlAuthenticationSecretKey;

  @Autowired
  @Qualifier("ThlSsoAuthenticationProvider")
  private ThlSsoRestAuthenticationProvider thlSsoRestAuthenticationProvider;

  @Bean
  public UserDetailsService propertiesBasedUserDetailsService() throws IOException {
    Properties properties = PropertiesLoaderUtils.loadProperties(
      new EncodedResource(usersPropertiesResource, "UTF-8"));

    LOG.info("Loaded {} users from {}", properties.size(), usersPropertiesResource);

    return new UserWithProfileUserDetailsManager(
      UserDirectory.LOCAL,
      new InMemoryUserDetailsManager(properties));
  }

  @Autowired
  public void configureAuthenticationManager(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.userDetailsService(propertiesBasedUserDetailsService());
    authManagerBuilder.authenticationProvider(thlSsoRestAuthenticationProvider);
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
        .antMatchers(HttpMethod.PATCH).authenticated()
        .antMatchers("/api/v3/editor/**").authenticated()
        .antMatchers("/editor/**").authenticated()
        .antMatchers("/login").permitAll()
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
      .formLogin()
        .loginPage("/login")
          .permitAll()
        .loginProcessingUrl("/api/v3/user-functions/login")
          .permitAll()
        .successHandler(new JsonBooleanResponseHandler(true))
        .failureHandler(new JsonBooleanResponseHandler(false))
        .and()
      .logout()
        .logoutUrl("/api/v3/user-functions/logout")
          .permitAll()
        .logoutSuccessHandler(new JsonBooleanResponseHandler(true))
        .and()
      .exceptionHandling()
        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        });
  }

  private static class JsonBooleanResponseHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler, LogoutSuccessHandler {
    private final boolean value;

    public JsonBooleanResponseHandler(boolean value) {
      this.value = value;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
      handleResponse(response);
    }

    private void handleResponse(HttpServletResponse response) throws IOException {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
      response.getWriter().write(Boolean.toString(this.value));
      response.getWriter().flush();
      response.getWriter().close();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      handleResponse(response);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
      handleResponse(response);
    }
  }

  @Bean
  @Qualifier("ThlSsoAuthenticationProvider")
  public ThlSsoRestAuthenticationProvider thlSsoRestAuthenticationProvider() {
    return new ThlSsoRestAuthenticationProvider(thlAuthenticationUrl,
      thlAuthenticationApplication, thlAuthenticationSecretKey);
  }

}
