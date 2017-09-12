package fi.thl.thldtkk.api.metadata.domain.termed;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Optional;

public class TypeId {

  private String id;
  private Graph graph;

  public TypeId(String id) {
    this(id, null);
  }

  public TypeId(String id, Graph graph) {
    this.id = id;
    this.graph = graph;
  }

  public String getId() {
    return id;
  }

  public Optional<Graph> getGraph() {
    return Optional.ofNullable(graph);
  }

  public void setGraph(Graph graph) {
    this.graph = graph;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("graph", graph)
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
    TypeId typeId = (TypeId) o;
    return Objects.equals(id, typeId.id)
      && Objects.equals(graph, graph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, graph);
  }

}
