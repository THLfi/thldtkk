package fi.thl.thldtkk.api.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.thl.thldtkk.api.metadata.util.SpringfoxJsonToGsonAdapter;
import fi.thl.thldtkk.api.metadata.util.json.LocalDateTypeAdapter;
import fi.thl.thldtkk.api.metadata.util.json.MultimapTypeAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.json.Json;

import java.time.LocalDate;
import java.util.Date;

@Configuration
public class GsonConfiguration {

  @Bean
  public Gson gson() {
    return new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Date.class, new GsonDateTypeAdapter())
        .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter().nullSafe())
        .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
        .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter())
        .create();
  }

}
