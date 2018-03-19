package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.UnitType;

import java.io.IOException;
import java.util.Optional;

public class DatasetUnitTypeSerializer extends JsonSerializer<Optional<UnitType>> {

  private LangValueSerializer langValueSerializer;

  public DatasetUnitTypeSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }

  @Override
  public void serialize(Optional<UnitType> datasetUnitType, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(datasetUnitType.isPresent()){
      this.langValueSerializer.serialize(datasetUnitType.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("dataset.unitType.id");
      jg.writeString(datasetUnitType.get().getId().toString());

      jg.writeFieldName("dataset.unitType.description");
      this.langValueSerializer.serialize(datasetUnitType.get().getDescription(), jg, sp);
    }

    else {
      jg.writeNull();
      jg.writeFieldName("dataset.unitType.id");
      jg.writeNull();
      jg.writeFieldName("dataset.unitType.description");
      jg.writeNull();
    }
  }

}
