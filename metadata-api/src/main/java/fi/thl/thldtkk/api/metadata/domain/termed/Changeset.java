package fi.thl.thldtkk.api.metadata.domain.termed;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

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

  public Changeset<K, V> merge(Changeset<K, V> another) {
    return new Changeset<>(
      ImmutableList.<K>builder().addAll(delete).addAll(another.delete).build(),
      ImmutableList.<V>builder().addAll(save).addAll(another.save).build());
  }

  public Changeset() {
  }

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
