package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Study;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class StudySerializer extends JsonSerializer<Optional<Study>> {

  private LangValueSerializer langValueSerializer;

  public StudySerializer() {
    this.langValueSerializer = new LangValueSerializer();
  }

  @Override
  public void serialize(Optional<Study> study, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if (study.isPresent()) {
      Optional<LocalDate> studyReferencePeriodStart = study.get().getReferencePeriodStart();
      Optional<LocalDate> studyReferencePeriodEnd = study.get().getReferencePeriodEnd();

      this.langValueSerializer.serialize(study.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("study.referencePeriodStart");
      if(studyReferencePeriodStart.isPresent()) {
        jg.writeString(studyReferencePeriodStart.get().toString());
      } else {
        jg.writeNull();
      }

      jg.writeFieldName("study.referencePeriodEnd");
      if(studyReferencePeriodEnd.isPresent()) {
        jg.writeString(studyReferencePeriodEnd.get().toString());
      } else {
        jg.writeNull();
      }
    } else {
      jg.writeNull();
      jg.writeFieldName("study.referencePeriodStart");
      jg.writeNull();
      jg.writeFieldName("study.referencePeriodEnd");
      jg.writeNull();
    }
  }
}
