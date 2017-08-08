package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Population;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class PopulationBuilder {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public PopulationBuilder() {
    withPrefLabel(PopulationBuilder.class.getSimpleName());
  }

  public PopulationBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public PopulationBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public PopulationBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public PopulationBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public Population build() {
    return new Population(id, prefLabel);
  }

}
