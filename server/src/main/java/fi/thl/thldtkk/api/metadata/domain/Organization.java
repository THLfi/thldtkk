package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Organization {

  private UUID id;

  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private String virtuId;
  private List<OrganizationUnit> organizationUnit = new ArrayList<>();

  public Organization() { }

  public Organization(UUID id) {
    this.id = requireNonNull(id);
  }

  public Organization(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "Organization"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.virtuId = PropertyMappings.toString(node.getProperties("virtuId"));
    node.getReferences("organizationUnit")
      .forEach(v -> this.organizationUnit.add(new OrganizationUnit(v)));
  }

  public Organization(UUID id,
                      Map<String, String> prefLabel,
                      Map<String, String> abbreviation,
                      String virtuId) {
    this.id = id;
    this.prefLabel = prefLabel;
    this.abbreviation = abbreviation;
    this.virtuId = virtuId;
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

  public Optional<String> getVirtuId() {
    return Optional.ofNullable(virtuId);
  }

  public List<OrganizationUnit> getOrganizationUnit() {
    return organizationUnit;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();

    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("abbreviation", toPropertyValues(abbreviation));
    getVirtuId().ifPresent(vi -> props.put("virtuId", toPropertyValue(vi)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOrganizationUnit().forEach(ou -> refs.put("organizationUnit", ou.toNode()));

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
      Objects.equals(virtuId, that.virtuId) &&
      Objects.equals(organizationUnit, that.organizationUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, abbreviation, virtuId, organizationUnit);
  }

}
