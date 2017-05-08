package fi.thl.thldtkk.api.metadata.domain.termed;

import com.google.common.base.MoreObjects;
import java.util.Objects;

public class TypeId {

  private String id;

  public TypeId(String id) {
    this.id = id;
  }

  public String getId() {
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
    TypeId typeId = (TypeId) o;
    return Objects.equals(id, typeId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
