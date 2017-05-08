package fi.thl.thldtkk.api.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.thl.thldtkk.api.metadata.util.json.DateTypeAdapter;
import fi.thl.thldtkk.api.metadata.util.json.MultimapTypeAdapterFactory;
import java.util.Date;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfiguration {

  @Bean
  public Gson gson() {
    return new GsonBuilder().setPrettyPrinting()
      .registerTypeAdapter(Date.class, new DateTypeAdapter().nullSafe())
      .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
      .create();
  }

}
