package fi.thl.thldtkk.api.metadata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TermedConfiguration {
    @Value("${termed.baseUrl}")
    private String baseUrl;
    @Value("${termed.user}")
    private String user;
    @Value("${termed.password}")
    private String password;

    @Bean
    public RestTemplate termedRestTemplate() {
        return new RestTemplateBuilder()
                .rootUri(baseUrl)
                .basicAuthorization(user, password)
                .build();
    }
}
