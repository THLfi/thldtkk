package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InstanceVariable {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();

  public InstanceVariable(UUID id) {
    this.id = requireNonNull(id);
  }

  public InstanceVariable(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "InstanceVariable"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.prefLabel = toLangValueMap(node.getProperties("description"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public Node toNode() {
    Node node = new Node(id, "InstanceVariable");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("description", toPropertyValues(description));
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
    InstanceVariable that = (InstanceVariable) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(prefLabel, that.prefLabel) &&
        Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, description);
  }

}
