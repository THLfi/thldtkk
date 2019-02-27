package fi.thl.thldtkk.api.metadata.domain;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class SupplementaryPhysicalSecurityPrinciple implements NodeEntity {

  public static String TERMED_NODE_CLASS = "SupplementaryPhysicalSecurityPrinciple";

  private UUID id;

  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public SupplementaryPhysicalSecurityPrinciple() {

  }

  public SupplementaryPhysicalSecurityPrinciple(UUID id) {
    this.id = requireNonNull(id);
  }

  public SupplementaryPhysicalSecurityPrinciple(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "SupplementaryPhysicalSecurityPrinciple"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  public SupplementaryPhysicalSecurityPrinciple(UUID id, Map<String, String> prefLabel, Map<String, String> linkUrl) {
    this.id = requireNonNull(id);
    this.prefLabel = prefLabel;
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

  public Node toNode() {
    Node node = new Node(id, "SupplementaryPhysicalSecurityPrinciple");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
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

    SupplementaryPhysicalSecurityPrinciple that = (SupplementaryPhysicalSecurityPrinciple) o;
    return Objects.equals(id, that.id) && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
}
