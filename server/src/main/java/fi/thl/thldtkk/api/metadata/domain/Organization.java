package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Organization {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private List<OrganizationUnit> organizationUnit = new ArrayList<>();

  public Organization() {

  }

  public Organization(UUID id) {
    this.id = requireNonNull(id);
  }

  public Organization(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "Organization"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    node.getReferences("organizationUnit")
      .forEach(v -> this.organizationUnit.add(new OrganizationUnit(v)));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public List<OrganizationUnit> getOrganizationUnit() {
    return organizationUnit;
  }

  public Node toNode() {
    Node node = new Node(id, "Organization");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("abbreviation", toPropertyValues(abbreviation));
    getOrganizationUnit().forEach(v -> node.addReference("organizationUnit", v.toNode()));
    return node;
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
      Objects.equals(organizationUnit, that.organizationUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, organizationUnit);
  }

}
