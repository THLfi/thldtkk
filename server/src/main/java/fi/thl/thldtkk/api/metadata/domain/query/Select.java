package fi.thl.thldtkk.api.metadata.domain.query;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.List;

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

}
