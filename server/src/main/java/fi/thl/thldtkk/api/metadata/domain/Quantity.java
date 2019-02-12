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

public class Quantity {

  public static final String TERMED_NODE_CLASS = "Quantity";

  private UUID id;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public Quantity(UUID id) {
    this.id = requireNonNull(id);
  }

  public Quantity(UUID id, Map<String, String> prefLabel) {
    this(id);
    this.prefLabel = prefLabel;
  }

  public Quantity(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Node toNode() {
    Node node = new Node(id, TERMED_NODE_CLASS);
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
    Quantity that = (Quantity) o;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, prefLabel);
  }

}
