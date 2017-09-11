package fi.thl.thldtkk.api.metadata.util.spring;

import com.google.gson.Gson;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Small factory class for building {@code GsonHttpMessageConverter} with given Gson instance in a
 * single call.
 */
public final class GsonHttpMessageConverterFactory {

  private GsonHttpMessageConverterFactory() {
  }

  public static GsonHttpMessageConverter build(Gson gson) {
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(gson);
    return gsonHttpMessageConverter;
  }

}
