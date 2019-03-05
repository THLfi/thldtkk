package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.CodeList;

import java.io.IOException;
import java.util.Optional;

public class CodeListSerializer extends JsonSerializer<Optional<CodeList>> {

  private LangValueSerializer langValueSerializer;
  private OptionalStringSerializer optionalStringSerializer;
  private CodeItemSerializer codeItemSerializer;

  public CodeListSerializer() {
    this.langValueSerializer = new LangValueSerializer();
    this.optionalStringSerializer = new OptionalStringSerializer();
    this.codeItemSerializer = new CodeItemSerializer();
  }

  @Override
  public void serialize(Optional<CodeList> codeList, JsonGenerator jg, SerializerProvider sp) throws IOException {
    if (codeList.isPresent()) {
      this.langValueSerializer.serialize(codeList.get().getPrefLabel(), jg, sp);

      jg.writeFieldName("codeList.id");
      jg.writeString(codeList.get().getId().toString());

      jg.writeFieldName("codeList.description");
      this.langValueSerializer.serialize(codeList.get().getDescription(), jg, sp);

      jg.writeFieldName("codeList.codeListType");
      this.optionalStringSerializer.serialize(codeList.get().getCodeListType(), jg, sp);

      jg.writeFieldName("codeList.referenceId");
      this.optionalStringSerializer.serialize(codeList.get().getReferenceId(), jg, sp);

      jg.writeFieldName("codeList.owner");
      this.langValueSerializer.serialize(codeList.get().getOwner(), jg, sp);

      jg.writeFieldName("codeList.codeItems");
      this.codeItemSerializer.serialize(codeList.get().getCodeItems(), jg, sp);
    }
    else {
      jg.writeNull(); // prefLabel

      jg.writeFieldName("codeList.id");
      jg.writeNull();

      jg.writeFieldName("codeList.description");
      jg.writeNull();

      jg.writeFieldName("codeList.codeListType");
      jg.writeNull();

      jg.writeFieldName("codeList.referenceId");
      jg.writeNull();

      jg.writeFieldName("codeList.owner");
      jg.writeNull();

      jg.writeFieldName("codeList.codeItems");
      jg.writeNull();
    }
  }

}
