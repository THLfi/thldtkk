package fi.thl.thldtkk.api.metadata.domain.termed;

import java.util.Objects;
import java.util.UUID;

public class NodeId {

  private UUID id;
  private TypeId type;

  public NodeId(UUID id, TypeId type) {
    this.id = id;
    this.type = type;
  }

  public NodeId(UUID id, String typeId) {
    this.id = id;
    this.type = new TypeId(typeId);
  }

  public NodeId(Node node) {
    this.id = node.getId();
    this.type = node.getType();
  }

  public UUID getId() {
    return id;
  }

  public TypeId getType() {
    return type;
  }

  public String getTypeId() {
    return type != null ? type.getId() : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NodeId nodeId = (NodeId) o;
    return Objects.equals(id, nodeId.id) &&
      Objects.equals(type, nodeId.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type);
  }

}
