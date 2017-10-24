package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Optional;

public class OptionalToStringSerializer extends JsonSerializer<Optional<Object>> {

  @Override
  public void serialize(Optional<Object> optionalObject, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(optionalObject.isPresent()) {
      jg.writeString(optionalObject.get().toString());
    }
    
    else {
      jg.writeNull();
    }
    
  }
  
}
