package fi.thl.thldtkk.api.metadata.domain.termed;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;

public class Graph {

  private UUID id;

  public Graph(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Graph graph = (Graph) o;
    return Objects.equals(id, graph.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
