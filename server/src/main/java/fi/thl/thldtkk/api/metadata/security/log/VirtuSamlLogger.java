package fi.thl.thldtkk.api.metadata.security.log;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.log.SAMLLogger;

/**
 * See also {@link org.springframework.security.saml.log.SAMLDefaultLogger}.
 */
public class VirtuSamlLogger implements SAMLLogger {

  private SAMLDefaultLogger samlDefaultLogger = new SAMLDefaultLogger();

  private static final Logger log = LoggerFactory.getLogger(VirtuSamlLogger.class);

  @Override
  public void log(String operation, String result, SAMLMessageContext context) {
    log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), null);
  }

  @Override
  public void log(String operation, String result, SAMLMessageContext context, Exception e) {
    log(operation, result, context, SecurityContextHolder.getContext().getAuthentication(), e);
  }

  @Override
  public void log(String operation, String result, SAMLMessageContext context, Authentication a, Exception e) {
    if (!log.isInfoEnabled()) {
      return;
    }

    operation = StringUtils.trimToEmpty(operation);
    result = StringUtils.trimToEmpty(result);
    if (context == null) {
      context = new SAMLMessageContext();
    }

    String userIpAddress = "";
    if (context.getInboundMessageTransport() != null) {
      HTTPInTransport transport = (HTTPInTransport) context.getInboundMessageTransport();
      userIpAddress = transport.getPeerAddress();
    }

    String idpEntityId = "";
    if (context.getPeerEntityId() != null) {
      idpEntityId = context.getPeerEntityId();
    }

    String userId = (a != null && a.getPrincipal() != null) ? a.getPrincipal().toString() : "<unknown>";

    if ("AuthNRequest".equals(operation)) {
      log.info("SECURITY: Authentication request: user IP = {}, IdP entity ID = {}", userIpAddress, idpEntityId, e != null ? e : "");
    }
    else if ("AuthNResponse".equals(operation)) {
      log.info("SECURITY: Authentication result: {}, userId = {}, user IP = {}, IdP entity ID = {}", result, userId, userIpAddress, idpEntityId, e != null ? e : "");
    }
    else {
      samlDefaultLogger.log(operation, result, context, a, e);
    }
  }

}
