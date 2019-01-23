package fi.thl.thldtkk.api.metadata;

import static com.google.common.base.Strings.nullToEmpty;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import fi.thl.thldtkk.api.metadata.controller.API;
import fi.thl.thldtkk.api.metadata.security.UserRoles;
import fi.thl.thldtkk.api.metadata.security.thlsso.ThlSsoRestAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("propertiesBasedUserDetailsService")
  private UserDetailsService propertiesBasedUserDetailsService;

  @Autowired
  private ThlSsoRestAuthenticationProvider thlSsoRestAuthenticationProvider;

  @Autowired
  public void configureAuthenticationManager(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.userDetailsService(propertiesBasedUserDetailsService);
    authManagerBuilder.authenticationProvider(thlSsoRestAuthenticationProvider);
  }

  @Configuration
  @Order(1)
  public static class HttpBasicSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .requestMatcher(req -> nullToEmpty(req.getHeader("Authorization")).startsWith("Basic "))
          .authorizeRequests()
            .anyRequest().hasAnyAuthority(UserRoles.SYSTEM)
            .and()
          .csrf()
            .disable()
          .httpBasic();
    }
  }

  @Configuration
  @EnableGlobalMethodSecurity(prePostEnabled = true)
  @Order(2)
  public static class FormLoginSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
          .csrf()
            .ignoringAntMatchers("/saml/**")
            .ignoringAntMatchers("/Virtu/**")
            // Set "XSRF-TOKEN" cookie which Angular's HTTP components can use out-of-the-box. For more info, see:
            // https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
            // https://angular.io/guide/http#security-xsrf-protection
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .and()
          .authorizeRequests()
            .antMatchers("/saml/**").permitAll()
            .antMatchers("/Virtu/**").permitAll()
            .antMatchers(API.PATH_WITH_VERSION + "/editor/**").authenticated()
            .antMatchers(HttpMethod.POST).authenticated()
            .antMatchers(HttpMethod.PUT).authenticated()
            .antMatchers(HttpMethod.DELETE).authenticated()
            .antMatchers(HttpMethod.PATCH).authenticated()
            .antMatchers(HttpMethod.HEAD).permitAll()
            .antMatchers(HttpMethod.GET).permitAll()
            .anyRequest().denyAll()
            .and()
          .formLogin()
            .loginPage("/login")
              .permitAll()
            .loginProcessingUrl(API.PATH_WITH_VERSION + "/user-functions/login")
              .permitAll()
              .successHandler((req, res, auth) -> res.setStatus(SC_NO_CONTENT))
              .failureHandler((req, res, e) -> res.sendError(SC_UNAUTHORIZED))
            .and()
          .logout()
            .logoutUrl(API.PATH_WITH_VERSION + "/user-functions/logout")
              .permitAll()
            .logoutSuccessHandler((req, res, e) -> res.setStatus(SC_NO_CONTENT))
            .and()
          .exceptionHandling()
            .authenticationEntryPoint(new Http401AuthenticationEntryPoint("unauthorized"))
            .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN));
    }
  }

}
