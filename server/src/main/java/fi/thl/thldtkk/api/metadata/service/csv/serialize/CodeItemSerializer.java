package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.CodeItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CodeItemSerializer extends JsonSerializer<List<CodeItem>> {

  @Override
  public void serialize(List<CodeItem> codeItems, JsonGenerator jg, SerializerProvider sp) throws IOException {
    if (codeItems.isEmpty()) {
      jg.writeNull();
    }
    else {
      Iterator<CodeItem> itemIterator = codeItems.iterator();
      StringBuilder serializedCodeItems = new StringBuilder();
      String language = sp.getLocale().getLanguage();

      while (itemIterator.hasNext()) {
        CodeItem codeItem = itemIterator.next();

        String code = codeItem.getCode().orElse("");
        serializedCodeItems.append(code);
        serializedCodeItems.append(":");

        String prefLabel = codeItem.getPrefLabel().containsKey(language) ? codeItem.getPrefLabel().get(language) : "";
        serializedCodeItems.append(prefLabel);

        if (itemIterator.hasNext()) {
          serializedCodeItems.append(ConceptSerializer.SEPARATOR);
        }
      }

      jg.writeString(serializedCodeItems.toString());
    }
  }

}
