package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Unit;
import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class UnitBuilder {
  
  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> symbol = new LinkedHashMap<>();

  public UnitBuilder() {
  
  }  

  public UnitBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public UnitBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public UnitBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public UnitBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
  
  public UnitBuilder withSymbol(Map<String, String> symbol) {
    this.symbol = symbol;
    return this;
  }

  public UnitBuilder withSymbol(String symbolValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", symbolValue);
    return withSymbol(langValues);
  }

  public Unit build() {
    return new Unit(id, prefLabel, symbol);
  }
}
