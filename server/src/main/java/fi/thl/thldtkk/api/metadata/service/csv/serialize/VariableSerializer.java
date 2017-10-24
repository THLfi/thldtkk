package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Variable;
import java.io.IOException;
import java.util.Optional;

public class VariableSerializer extends JsonSerializer<Optional<Variable>>{

  private LangValueSerializer langValueSerializer;
  
  public VariableSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<Variable> variable, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(variable.isPresent()){
      this.langValueSerializer.serialize(variable.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("variable.id");
      jg.writeString(variable.get().getId().toString());
      
      jg.writeFieldName("variable.description");
      this.langValueSerializer.serialize(variable.get().getDescription(), jg, sp);
    }
    
    else {
      jg.writeNull();
      jg.writeFieldName("variable.id");
      jg.writeNull();
      jg.writeFieldName("variable.description");
      jg.writeNull();
    }

  }
  
}
