package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class InstanceVariableDatasetSerializer extends JsonSerializer<Optional<Dataset>>{

  private LangValueSerializer langValueSerializer;

  public InstanceVariableDatasetSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<Dataset> instanceVariableDataset, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(instanceVariableDataset.isPresent()) {
      Optional<LocalDate> referencePeriodStart = instanceVariableDataset.get().getReferencePeriodStart();
      Optional<LocalDate> referencePeriodEnd = instanceVariableDataset.get().getReferencePeriodEnd();

      this.langValueSerializer.serialize(instanceVariableDataset.get().getPrefLabel(), jg, sp);
      
      jg.writeFieldName("dataset.id");
      jg.writeString(instanceVariableDataset.get().getId().toString());
            
      jg.writeFieldName("dataset.referencePeriodStart");
      
      if(referencePeriodStart.isPresent()) {
        jg.writeString(referencePeriodStart.get().toString());
      }
      
      else {
        jg.writeNull();
      }
      
      jg.writeFieldName("dataset.referencePeriodEnd");
      
      if(referencePeriodEnd.isPresent()) {
        jg.writeString(referencePeriodEnd.get().toString());
      }
      
      else {
        jg.writeNull();
      }

      
    }
    
    else {
      jg.writeNull();
      jg.writeFieldName("dataset.id");
      jg.writeNull();
      jg.writeFieldName("dataset.referencePeriodStart");
      jg.writeNull();
      jg.writeFieldName("dataset.referencePeriodEnd");
      jg.writeNull();
    }
  }
  
}
