package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class VariableBuilder {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();

  public VariableBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public VariableBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public VariableBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public VariableBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public VariableBuilder withDescription(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public VariableBuilder withDescription(String descriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", descriptionValue);
    return withDescription(langValues);
  }

  public Variable build() {
    return new Variable(id, prefLabel, description);
  }
  
}
