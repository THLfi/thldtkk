package fi.thl.thldtkk.api.metadata;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements ApplicationListener<ObjectMapperConfigured> {

  @Bean
  public Docket productApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(or(
                RequestHandlerSelectors.withMethodAnnotation(GetJsonMapping.class),
                RequestHandlerSelectors.withMethodAnnotation(PostMapping.class)))
        .paths(or(
            regex("/api/v[0-9]+/editor/dataset-functions.*"),
            regex("/api/v[0-9]+/editor/datasets.*"),
            regex("/api/v[0-9]+/editor/instanceVariables.*"),
            regex("/api/v[0-9]+/editor/variables.*"),
            regex("/api/v[0-9]+/public/.*"),
            regex("/api/v[0-9]+/datasetTypes.*"),
            regex("/api/v[0-9]+/variables.*"),
            regex("/api/v[0-9]+/organizations.*")))
        .build()
        .apiInfo(new ApiInfo(
            "Metadata Service API",
            "Metadata Service API",
            "0.1",
            "",
            "",
            "EUPL 1.1",
            ""));
  }


  @Override
  public void onApplicationEvent(ObjectMapperConfigured objectMapperConfigured) {
    ObjectMapper mapper = objectMapperConfigured.getObjectMapper();

    // configure Jackson object mapper to only use fields, not getter/setters
    mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
  }

}
