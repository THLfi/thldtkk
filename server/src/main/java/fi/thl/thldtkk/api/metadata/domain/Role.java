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

public class Role {

  public static final String TERMED_NODE_CLASS = "Role";

  private UUID id;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public Role(UUID id) {
    this.id = requireNonNull(id);
  }

  /**
   * For testing purposes.
   */
  public Role(UUID id, Map<String, String> prefLabel) {
    this.id = id;
    this.prefLabel = prefLabel;
  }

  public Role(Node node) {
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
    Role that = (Role) o;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, prefLabel);
  }

}
