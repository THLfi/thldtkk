package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.UnitType;

import java.io.IOException;
import java.util.Optional;

public class StudyUnitTypeSerializer extends JsonSerializer<Optional<UnitType>> {

  private LangValueSerializer langValueSerializer;

  public StudyUnitTypeSerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }

  @Override
  public void serialize(Optional<UnitType> studyUnitType, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(studyUnitType.isPresent()){
      this.langValueSerializer.serialize(studyUnitType.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("study.unitType.id");
      jg.writeString(studyUnitType.get().getId().toString());

      jg.writeFieldName("study.unitType.description");
      this.langValueSerializer.serialize(studyUnitType.get().getDescription(), jg, sp);
    }

    else {
      jg.writeNull();
      jg.writeFieldName("study.unitType.id");
      jg.writeNull();
      jg.writeFieldName("study.unitType.description");
      jg.writeNull();
    }
  }

}
