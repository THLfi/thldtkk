package fi.thl.thldtkk.api.metadata.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import springfox.documentation.spring.web.json.Json;

public class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {

  private JsonParser jsonParser = new JsonParser();

  @Override
  public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
    return jsonParser.parse(json.value());
  }

}
