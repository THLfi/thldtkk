package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Quantity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class QuantityBuilder {

  private UUID id;
  private Map<String, String> prefLabel;

  public QuantityBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public QuantityBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public QuantityBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public QuantityBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public Quantity build() {
    return new Quantity(id, prefLabel);
  }

  
}
