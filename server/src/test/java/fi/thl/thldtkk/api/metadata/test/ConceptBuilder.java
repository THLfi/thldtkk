package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.domain.ConceptScheme;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;


public class ConceptBuilder {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private ConceptScheme conceptScheme;

  public ConceptBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public ConceptBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public ConceptBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public ConceptBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
    
  public ConceptBuilder withConceptScheme(ConceptScheme conceptScheme) {
    this.conceptScheme = conceptScheme;
    return this;
  }
  
  public Concept build() {
    return new Concept(id, prefLabel, conceptScheme);
  }
  
  

  
  
}
