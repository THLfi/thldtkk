package fi.thl.thldtkk.api.metadata;

import static com.google.common.base.Strings.nullToEmpty;

import fi.thl.thldtkk.api.metadata.security.JsonBooleanResponseHandler;
import fi.thl.thldtkk.api.metadata.security.UserRoles;
import fi.thl.thldtkk.api.metadata.security.thlsso.ThlSsoRestAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
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
            .antMatchers("/api/v3/editor/**").authenticated()
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
            .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN));
    }
  }

}
