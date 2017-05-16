package fi.thl.thldtkk.api.metadata;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import com.google.gson.Gson;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TermedConfiguration {

  @Value("${termed.apiUrl}")
  private String apiUrl;
  @Value("${termed.username}")
  private String username;
  @Value("${termed.password}")
  private String password;
  @Value("${termed.graphId}")
  private UUID graphId;

  @Autowired
  private Gson gson;

  @Bean
  public RestTemplate termedRestTemplate() {
    String baseUrl = fromHttpUrl(apiUrl)
      .path("/graphs")
      .path("/" + graphId.toString())
      .toUriString();

    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(gson);

    return new RestTemplateBuilder()
      .rootUri(baseUrl)
      .messageConverters(gsonHttpMessageConverter)
      .basicAuthorization(username, password)
      .build();
  }

}
