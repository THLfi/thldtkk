package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.UnitType;
import java.io.IOException;
import java.util.Optional;

public class UnitTypeSerializer extends JsonSerializer<Optional<UnitType>>{

  private LangValueSerializer langValueSerializer;
  
  public UnitTypeSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<UnitType> unitType, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(unitType.isPresent()){
      this.langValueSerializer.serialize(unitType.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("unitType.id");
      jg.writeString(unitType.get().getId().toString());
      
      jg.writeFieldName("unitType.description");
      this.langValueSerializer.serialize(unitType.get().getDescription(), jg, sp);
    }
    
    else {
      jg.writeNull();
      jg.writeFieldName("unitType.id");
      jg.writeNull();
      jg.writeFieldName("unitType.description");
      jg.writeNull();
    }
  }
  
}
