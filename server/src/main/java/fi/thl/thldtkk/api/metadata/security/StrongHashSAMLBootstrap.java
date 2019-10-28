package fi.thl.thldtkk.api.metadata.security;

import org.opensaml.xml.Configuration;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.signature.SignatureConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.security.saml.SAMLBootstrap;

public final class StrongHashSAMLBootstrap extends SAMLBootstrap {

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    super.postProcessBeanFactory(beanFactory);
    BasicSecurityConfiguration securityConfig = (BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration();
    securityConfig.registerSignatureAlgorithmURI("RSA", SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
    securityConfig.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
  }
}
