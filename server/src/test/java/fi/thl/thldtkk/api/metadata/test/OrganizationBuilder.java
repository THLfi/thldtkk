package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrganizationBuilder {
  
  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public OrganizationBuilder() {
    withPrefLabel(OrganizationBuilder.class.getSimpleName());
  }
 
  public OrganizationBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public OrganizationBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public OrganizationBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public OrganizationBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public Organization build() {
    return new Organization(id, prefLabel);
  }
  
}