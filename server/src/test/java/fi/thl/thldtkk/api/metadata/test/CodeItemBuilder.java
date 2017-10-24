package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class CodeItemBuilder {

  private UUID id;
  private String code;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  
  public CodeItemBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public CodeItemBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public CodeItemBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public CodeItemBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
  
  public CodeItemBuilder withCode(String code) {
    this.code = code;
    return this;
  }
  
  public CodeItem build() {
    return new CodeItem(id, code, prefLabel);
  }
  
}
