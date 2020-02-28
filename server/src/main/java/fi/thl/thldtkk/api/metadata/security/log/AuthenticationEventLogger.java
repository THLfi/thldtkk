package fi.thl.thldtkk.api.metadata.security.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuthenticationEventLogger implements ApplicationListener<AbstractAuthenticationEvent> {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationEventLogger.class);

  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {
    if (event.getAuthentication().getCredentials() instanceof SAMLCredential) {
      // Do nothing, SAML logins are logged separately
      return;
    }

    Object userId = event.getAuthentication().getPrincipal();
    String userIpAddress = "<unknown>";
    if (event.getAuthentication().getDetails() instanceof WebAuthenticationDetails) {
      userIpAddress = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();
    }

    if (event instanceof InteractiveAuthenticationSuccessEvent) {
      log.info("SECURITY: User logged in: userId = {}, IP = {}", userId, userIpAddress);
    }
    else if (event instanceof AbstractAuthenticationFailureEvent) {
      AbstractAuthenticationFailureEvent failureEvent = (AbstractAuthenticationFailureEvent) event;
      log.info("SECURITY: User failed to login: userId = {}, IP = {}, reason = {}", userId, userIpAddress,
        failureEvent.getException().getMessage());
    }
  }

}
