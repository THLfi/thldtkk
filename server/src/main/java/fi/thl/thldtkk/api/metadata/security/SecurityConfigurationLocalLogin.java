package fi.thl.thldtkk.api.metadata.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class SecurityConfigurationLocalLogin {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityConfigurationLocalLogin.class);

  @Value("${users.properties.resource}")
  private Resource usersPropertiesResource;

  @Autowired
  private UserProfileHelper userProfileHelper;

  @Bean
  public UserDetailsService propertiesBasedUserDetailsService() throws IOException {
    Properties properties = PropertiesLoaderUtils.loadProperties(
      new EncodedResource(usersPropertiesResource, "UTF-8"));

    LOG.info("Loaded {} users from {}", properties.size(), usersPropertiesResource);

    return new UserWithProfileUserDetailsManager(
      userProfileHelper,
      UserDirectory.LOCAL,
      new InMemoryUserDetailsManager(properties));
  }

}
