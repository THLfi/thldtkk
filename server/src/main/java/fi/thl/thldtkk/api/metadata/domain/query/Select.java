package fi.thl.thldtkk.api.metadata.domain.query;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Objects;

public class Select {

  private List<String> fields;

  public Select(List<String> fields) {
    this.fields = fields;
  }

  public static Select select(String... fields) {
    return new Select(asList(fields));
  }

  /**
   * Same as Select.select("*")
   */
  public static Select selectAll() {
    return new Select(singletonList("*"));
  }

  public List<String> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return String.join(",", fields);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + Objects.hashCode(this.fields);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Select other = (Select) obj;
    if (!Objects.equals(this.fields, other.fields)) {
      return false;
    }
    return true;
  }

  
  
}
