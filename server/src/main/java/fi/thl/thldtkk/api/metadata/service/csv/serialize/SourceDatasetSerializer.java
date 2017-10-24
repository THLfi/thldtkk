package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import java.io.IOException;
import java.util.Optional;

public class SourceDatasetSerializer extends JsonSerializer<Optional<Dataset>>{

  private LangValueSerializer langValueSerializer;
  
  public SourceDatasetSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }
  
  @Override
  public void serialize(Optional<Dataset> sourceDataset, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(sourceDataset.isPresent()) {
      this.langValueSerializer.serialize(sourceDataset.get().getPrefLabel(), jg, sp);
      jg.writeFieldName("source.dataset.id");
      jg.writeString(sourceDataset.get().getId().toString());
    }
    
    else {
      jg.writeNull();
      jg.writeFieldName("source.dataset.id");
      jg.writeNull();
    }
  }
  
}
