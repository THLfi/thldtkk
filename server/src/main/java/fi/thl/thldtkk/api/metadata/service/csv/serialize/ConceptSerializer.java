package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fi.thl.thldtkk.api.metadata.domain.Concept;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ConceptSerializer extends JsonSerializer<List<Concept>> {

  @Override
  public void serialize(List<Concept> concepts, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
    if(concepts != null && !concepts.isEmpty()) {
      Iterator<Concept> itemIterator = concepts.iterator();
      StringBuilder serializedConcepts = new StringBuilder();
      String language = sp.getLocale().getLanguage();
      
      while(itemIterator.hasNext()) {
        Concept concept = itemIterator.next();
        
        String prefLabel = concept.getPrefLabel().containsKey(language) ? concept.getPrefLabel().get(language) : "";
        String conceptScheme = concept.getConceptScheme().isPresent() && 
                concept.getConceptScheme().get().getPrefLabel().containsKey(language) ?
                concept.getConceptScheme().get().getPrefLabel().get(language) : "";
        
        
        serializedConcepts.append("'");
        serializedConcepts.append(prefLabel);
        serializedConcepts.append(" <");
        serializedConcepts.append(conceptScheme);
        serializedConcepts.append(">");
        serializedConcepts.append("'");
        
        if(itemIterator.hasNext()) {
          serializedConcepts.append(", ");
        }
      }
      
      jg.writeString(serializedConcepts.toString());

    }
    
    else {
      jg.writeNull();
    }
  }
  
}
