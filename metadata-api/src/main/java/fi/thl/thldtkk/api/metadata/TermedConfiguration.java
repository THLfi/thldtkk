package fi.thl.thldtkk.api.metadata;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    @Bean
    public RestTemplate termedRestTemplate() {
        String baseUrl = fromHttpUrl(apiUrl)
            .path("/graphs")
            .path("/" + graphId.toString())
            .toUriString();

        return new RestTemplateBuilder()
                .rootUri(baseUrl)
                .basicAuthorization(username, password)
                .build();
    }
    
}
