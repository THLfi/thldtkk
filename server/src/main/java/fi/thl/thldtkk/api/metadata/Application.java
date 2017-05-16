package fi.thl.thldtkk.api.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.thl.thldtkk.api.metadata.util.json.DateTypeAdapter;
import fi.thl.thldtkk.api.metadata.util.json.MultimapTypeAdapterFactory;
import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Gson gson() {
    return new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Date.class, new DateTypeAdapter().nullSafe())
        .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
        .create();
  }

}
