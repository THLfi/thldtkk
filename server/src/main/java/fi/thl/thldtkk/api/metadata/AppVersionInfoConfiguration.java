package fi.thl.thldtkk.api.metadata;

import fi.thl.thldtkk.api.metadata.domain.AppVersionInfo;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:/git.properties")
public class AppVersionInfoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(AppVersionInfoConfiguration.class);

  @Value("${git.build.version}")
  private String gitBuildVersion;
  @Value("${git.commit.id}")
  private String gitCommitId;
  @Value("${git.dirty}")
  private String gitDirty;

  @Bean
  public AppVersionInfo versionInfo() {
    return new AppVersionInfo(gitBuildVersion, gitCommitId, gitDirty);
  }

  @PostConstruct
  public void printAppVersionInfo() {
    log.info("App version: {}", ToStringBuilder.reflectionToString(versionInfo(), ToStringStyle.NO_CLASS_NAME_STYLE));
  }

}
