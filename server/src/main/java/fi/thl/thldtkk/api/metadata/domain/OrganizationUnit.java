package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class OrganizationUnit implements Serializable {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();

  /**
   * Required by GSON deserialization.
   */
  private OrganizationUnit() {

  }

  public OrganizationUnit(UUID id) {
    this.id = requireNonNull(id);
  }

  public OrganizationUnit(UUID id,
    Map<String, String> prefLabel,
    Map<String, String> abbreviation) {
    this(id);
    this.prefLabel = prefLabel;
    this.abbreviation = abbreviation;
  }

  public OrganizationUnit(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "OrganizationUnit"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getAbbreviation() {
    return abbreviation;
  }

  public Node toNode() {
    Node node = new Node(id, "OrganizationUnit");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("abbreviation", toPropertyValues(abbreviation));
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
    OrganizationUnit that = (OrganizationUnit) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
}
