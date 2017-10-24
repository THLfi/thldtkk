package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.ConceptScheme;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class ConceptSchemeBuilder {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public ConceptSchemeBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public ConceptSchemeBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public ConceptSchemeBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public ConceptSchemeBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
  
  public ConceptScheme build() {
    return new ConceptScheme(id, prefLabel);
  }
  
}
