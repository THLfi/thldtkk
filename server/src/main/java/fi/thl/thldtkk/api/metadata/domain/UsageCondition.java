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

public class UsageCondition {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public UsageCondition(UUID id) {
    this.id = requireNonNull(id);
  }

  public UsageCondition(UUID id, Map<String, String> prefLabel) {
    this(id);
    this.prefLabel = prefLabel;
  }

  public UsageCondition(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "UsageCondition"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Node toNode() {
    Node node = new Node(id, "UsageCondition");
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
    UsageCondition that = (UsageCondition) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
}
