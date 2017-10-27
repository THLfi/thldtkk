package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class UnitTypeBuilder {
  
  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();

  public UnitTypeBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public UnitTypeBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public UnitTypeBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public UnitTypeBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public UnitTypeBuilder withDescription(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public UnitTypeBuilder withDescription(String descriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", descriptionValue);
    return withDescription(langValues);
  }

  public UnitType build() {
    return new UnitType(id, prefLabel, description);
  }
}
