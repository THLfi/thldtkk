package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Map;

public class LangValueSerializer extends JsonSerializer<Map<String, String>> {

  @Override
  public void serialize(Map<String, String> langValues, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    String language = sp.getLocale().getLanguage();
    
    if(langValues.containsKey(language)) {
      jg.writeString(langValues.get(language));
    }
    
    else {
      jg.writeNull();
    }
  }
  
}
