package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valuesToStringCollection;
import static java.util.Objects.requireNonNull;

public class Organization implements NodeEntity, Serializable {

  private UUID id;

  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private Map<String, String> addressForRegistryPolicy = new LinkedHashMap<>();
  // Marking field 'transient' excludes it from GSON (de)serialization
  private transient List<String> virtuIds = new ArrayList<>();
  private List<OrganizationUnit> organizationUnit = new ArrayList<>();
  private String phoneNumberForRegistryPolicy;

  public Organization() { }

  public Organization(UUID id) {
    this.id = requireNonNull(id);
  }

  public Organization(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "Organization"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.addressForRegistryPolicy = toLangValueMap(node.getProperties("addressForRegistryPolicy"));
    this.virtuIds = valuesToStringCollection(node.getProperties("virtuIds"), ArrayList::new);
    this.phoneNumberForRegistryPolicy = PropertyMappings.toString(node.getProperties("phoneNumberForRegistryPolicy"));
    node.getReferences("organizationUnit")
      .forEach(v -> this.organizationUnit.add(new OrganizationUnit(v)));
  }

  public Organization(UUID id,
                      Map<String, String> prefLabel,
                      Map<String, String> abbreviation,
                      Map<String, String> addressForRegistryPolicy,
                      String phoneNumberForRegistryPolicy,
                      List<String> virtuIds) {
    this.id = id;
    this.prefLabel = prefLabel;
    this.abbreviation = abbreviation;
    this.addressForRegistryPolicy = addressForRegistryPolicy;
    this.phoneNumberForRegistryPolicy = phoneNumberForRegistryPolicy;
    this.virtuIds = virtuIds;
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getAbbreviation() {
    return abbreviation;
  }

  public void setPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
  }

  public void setAbbreviation(Map<String, String> abbreviation) {
    this.abbreviation = abbreviation;
  }

  public List<String> getVirtuIds() {
    return virtuIds != null ? virtuIds : Collections.emptyList();
  }

  public List<OrganizationUnit> getOrganizationUnits() {
    return organizationUnit;
  }

  public void addOrganizationUnit(OrganizationUnit unit) {
    organizationUnit.add(unit);
  }

  public void removeOrganizationUnit(OrganizationUnit unit ) {
    organizationUnit.removeIf(currentUnit -> currentUnit.getId().equals(unit.getId()));
  }

  public Optional<String> getPhoneNumberForRegistryPolicy() {
    return Optional.ofNullable(phoneNumberForRegistryPolicy);
  }

  public Map<String, String> getAddressForRegistryPolicy(){
    return addressForRegistryPolicy;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();

    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("abbreviation", toPropertyValues(abbreviation));
    props.putAll("addressForRegistryPolicy", toPropertyValues(addressForRegistryPolicy));

    getVirtuIds().forEach(vi -> props.put("virtuIds", toPropertyValue(vi)));
    getPhoneNumberForRegistryPolicy().ifPresent(v -> props.put("phoneNumberForRegistryPolicy", toPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOrganizationUnits().forEach(ou -> refs.put("organizationUnit", ou.toNode()));

    return new Node(id, "Organization", props, refs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Organization that = (Organization) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(prefLabel, that.prefLabel) &&
      Objects.equals(abbreviation, that.abbreviation) &&
      Objects.equals(virtuIds, that.virtuIds) &&
      Objects.equals(organizationUnit, that.organizationUnit) &&
      Objects.equals(phoneNumberForRegistryPolicy, that.phoneNumberForRegistryPolicy) &&
      Objects.equals(addressForRegistryPolicy, that.addressForRegistryPolicy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, abbreviation, virtuIds, organizationUnit, phoneNumberForRegistryPolicy,
        addressForRegistryPolicy);
  }

}
