package fi.thl.thldtkk.api.metadata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;

@Configuration
public class ThymeleafConfiguration {

  @Bean
  public TemplateEngine thymeleafTemplateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.addDialect(new Java8TimeDialect());
    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
    templateEngine.setTemplateEngineMessageSource(thymeleafTranslationsMessageSource());
    return templateEngine;
  }

  @Bean
  public SpringResourceTemplateResolver thymeleafTemplateResolver() {
    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setTemplateMode(StandardTemplateModeHandlers.VALIDXHTML.getTemplateModeName());
    templateResolver.setPrefix("classpath:/thymeleaf-templates/");
    templateResolver.setSuffix(".xhtml");
    return templateResolver;
  }

  @Bean
  public ResourceBundleMessageSource thymeleafTranslationsMessageSource() {
    ResourceBundleMessageSource source = new ResourceBundleMessageSource();
    source.setBasenames("i18n/register-description", "i18n/privacy-notice");
    source.setDefaultEncoding("UTF-8");
    source.setFallbackToSystemLocale(false);
    return source;
  }

}
