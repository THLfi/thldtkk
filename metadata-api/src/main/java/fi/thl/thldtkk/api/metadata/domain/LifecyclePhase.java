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

public class LifecyclePhase {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public LifecyclePhase(UUID id) {
    this.id = requireNonNull(id);
  }

  public LifecyclePhase(UUID id, Map<String, String> prefLabel) {
    this(id);
    this.prefLabel = prefLabel;
  }

  public LifecyclePhase(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "LifecyclePhase"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Node toNode() {
    Node node = new Node(id, "LifecyclePhase");
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
    LifecyclePhase that = (LifecyclePhase) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
}
