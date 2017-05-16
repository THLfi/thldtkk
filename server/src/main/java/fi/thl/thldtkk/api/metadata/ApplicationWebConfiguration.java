package fi.thl.thldtkk.api.metadata;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ApplicationWebConfiguration extends WebMvcConfigurerAdapter {

  @Autowired
  private Gson gson;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(gson);
    converters.addAll(Collections.singletonList(gsonHttpMessageConverter));
    super.configureMessageConverters(converters);
  }

}
