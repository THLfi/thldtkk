package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Quantity;
import java.io.IOException;
import java.util.Optional;

public class QuantitySerializer extends JsonSerializer<Optional<Quantity>>{

  private LangValueSerializer langValueSerializer;
  
  public QuantitySerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<Quantity> quantity, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(quantity.isPresent()) {
      langValueSerializer.serialize(quantity.get().getPrefLabel(), jg, sp);
      jg.writeFieldName("quantity.id");
      jg.writeString(quantity.get().getId().toString());
    }
    
    else {
      jg.writeNull();
      jg.writeFieldName("quantity.id");
      jg.writeNull();
    }
  }
  
}
