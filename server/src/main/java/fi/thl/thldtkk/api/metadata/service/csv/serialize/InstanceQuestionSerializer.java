package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class InstanceQuestionSerializer extends JsonSerializer<List<InstanceQuestion>>{

  @Override
  public void serialize(List<InstanceQuestion> instanceQuestions, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
      if(instanceQuestions != null && !instanceQuestions.isEmpty()) {
      Iterator<InstanceQuestion> itemIterator = instanceQuestions.iterator();
      StringBuilder serializedQuestions = new StringBuilder();
      String language = sp.getLocale().getLanguage();
      
      while(itemIterator.hasNext()) {
        InstanceQuestion question = itemIterator.next();
        
        String prefLabel = question.getPrefLabel().containsKey(language) ? question.getPrefLabel().get(language) : "";        
        
        serializedQuestions.append("'");
        serializedQuestions.append(prefLabel);
        serializedQuestions.append("'");
        
        if(itemIterator.hasNext()) {
          serializedQuestions.append(", ");
        }
      }
      
      jg.writeString(serializedQuestions.toString());

    }
    
    else {
      jg.writeNull();
    }
  }
  
}
