package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import java.io.IOException;
import java.util.Optional;

public class UnitSerializer extends JsonSerializer<Optional<Unit>> {

  private LangValueSerializer langValueSerializer;
  
  public UnitSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<Unit> unit, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {    
    if(unit.isPresent()) {
      // default named property "unit.prefLabel"
      langValueSerializer.serialize(unit.get().getPrefLabel(), jg, sp);
            
      jg.writeFieldName("unit.id");
      jg.writeString(unit.get().getId().toString());
      
      jg.writeFieldName("unit.symbol");
      langValueSerializer.serialize(unit.get().getSymbol(), jg, sp);
    }
    
    else {
      jg.writeNull(); // prefLabel
      jg.writeFieldName("unit.id");
      jg.writeNull();
      jg.writeFieldName("unit.symbol");
      jg.writeNull();
    }
  }
  
}
