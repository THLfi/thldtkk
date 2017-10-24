package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Optional;

public class OptionalStringSerializer extends JsonSerializer<Optional<String>>{

  @Override
  public void serialize(Optional<String> optionalString, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    
    if(optionalString.isPresent()) {
      jg.writeString(optionalString.get());
    }
    
    else {
      jg.writeNull();
    }
    
  }
  
}
