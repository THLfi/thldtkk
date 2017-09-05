package fi.thl.thldtkk.api.metadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  public void configureAuthenticationManager(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder
      .inMemoryAuthentication()
      .withUser("user")
        .password("password")
        .authorities("AINEISTOEDITORI.FI_USER");
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
