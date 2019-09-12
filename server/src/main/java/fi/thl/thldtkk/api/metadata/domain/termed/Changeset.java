package fi.thl.thldtkk.api.metadata.domain.termed;

import static com.google.common.collect.Maps.difference;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import fi.thl.thldtkk.api.metadata.domain.NodeEntity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Changeset<K, V> {

  private List<K> delete = emptyList();
  private List<V> save = emptyList();

  public static <K, V> Changeset<K, V> empty() {
    return new Changeset<>();
  }

  public static <K, V> Changeset<K, V> delete(K node) {
    return new Changeset<>(singletonList(node));
  }

  public static <K, V> Changeset<K, V> save(V node) {
    return new Changeset<>(emptyList(), singletonList(node));
  }

  public static  <T extends NodeEntity> Changeset<NodeId, Node> buildChangeset(
    List<T> newNodeEntities,
    List<T> oldNodeEntities) {

    List<NodeId> deleted = getDeletedNodes(newNodeEntities, oldNodeEntities).stream()
      .map(T::toNode)
      .map(NodeId::new)
      .collect(toList());

    List<Node> saved = newNodeEntities.stream()
      .map(NodeEntity::toNode).collect(toList());

    return new Changeset<>(deleted, saved);
  }

  public static <T extends NodeEntity> List<T> getDeletedNodes(
    List<T> newNodeEntities,
    List<T> oldNodeEntities) {

    Map<UUID, T> newNodeEntitiesById = index(newNodeEntities, T::getId);
    Map<UUID, T> oldNodeEntitiesById = index(oldNodeEntities, T::getId);

    return difference(newNodeEntitiesById, oldNodeEntitiesById)
      .entriesOnlyOnRight()
      .values().stream().collect(Collectors.toList());
  }

  public Changeset() { }

  public Changeset(List<K> delete) {
    this.delete = delete;
  }

  public Changeset(List<K> delete, List<V> save) {
    this.delete = delete;
    this.save = save;
  }

  public List<K> getDelete() {
    return delete;
  }

  public List<V> getSave() {
    return save;
  }

  public Changeset<K, V> merge(Changeset<K, V> another) {
    return new Changeset<>(
      ImmutableList.<K>builder().addAll(delete).addAll(another.delete).build(),
      ImmutableList.<V>builder().addAll(save).addAll(another.save).build());
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("delete", delete)
      .add("save", save)
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
    Changeset that = (Changeset) o;
    return Objects.equals(delete, that.delete) &&
      Objects.equals(save, that.save);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delete, save);
  }

}
