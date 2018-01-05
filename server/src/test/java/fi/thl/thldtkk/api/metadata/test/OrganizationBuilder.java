package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Organization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Arrays.asList;

public class OrganizationBuilder {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private Map<String, String> addressForRegistryPolicy = new LinkedHashMap<>();
  private String phoneNumberForRegistryPolicy;
  private List<String> virtuIds = new ArrayList<>();

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

  public OrganizationBuilder withAbbreviation(Map<String, String> abbreviation) {
    this.abbreviation = abbreviation;
    return this;
  }

  public OrganizationBuilder withAbbreviation(String abbreviationValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", abbreviationValue);
    return withAbbreviation(langValues);
  }

  public OrganizationBuilder withAddressForRegistryPolicy(Map<String, String> addressForRegistryPolicy) {
    this.addressForRegistryPolicy = addressForRegistryPolicy;
    return this;
  }

  public OrganizationBuilder withAddressForRegistryPolicy(String addressForRegistryPolicyValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", addressForRegistryPolicyValue);
    return withAddressForRegistryPolicy(langValues);
  }

  public OrganizationBuilder withPhoneNumberForRegistryPolicy(String phoneNumberForRegistryPolicy) {
    this.phoneNumberForRegistryPolicy = phoneNumberForRegistryPolicy;
    return this;
  }

  public OrganizationBuilder withVirtuId(String... virtuIds) {
    this.virtuIds = asList(virtuIds);
    return this;
  }

  public Organization build() {
    return new Organization(
      id,
      prefLabel,
      abbreviation,
      addressForRegistryPolicy,
      phoneNumberForRegistryPolicy,
      virtuIds
    );
  }

}
