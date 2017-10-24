package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CodeItemSerializer extends JsonSerializer<List<CodeItem>> {
  
  @Override
  public void serialize(List<CodeItem> codeItems, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(!codeItems.isEmpty()) {
      Iterator<CodeItem> itemIterator = codeItems.iterator();
      StringBuilder serializedCodeItems = new StringBuilder();
      String language = sp.getLocale().getLanguage();
      
      while(itemIterator.hasNext()) {
        CodeItem codeItem = itemIterator.next();
        
        String code = codeItem.getCode().isPresent() ? codeItem.getCode().get() : "";
        String prefLabel = codeItem.getPrefLabel().containsKey(language) ? codeItem.getPrefLabel().get(language) : "";
        
        serializedCodeItems.append("'");
        serializedCodeItems.append(code);
        serializedCodeItems.append("'");
        
        serializedCodeItems.append(":");
        
        serializedCodeItems.append("'");
        serializedCodeItems.append(prefLabel);
        serializedCodeItems.append("'");
        
        if(itemIterator.hasNext()) {
          serializedCodeItems.append(", ");
        }
      }
      
      jg.writeString(serializedCodeItems.toString());
      
    }
    
    else {
      jg.writeNull();
    }
  }
  
}
