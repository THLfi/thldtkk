package fi.thl.thldtkk.api.metadata.domain.query;

import java.util.Arrays;
import java.util.List;

public class Sort {

  private List<String> fields;

  public Sort(List<String> fields) {
    this.fields = fields;
  }

  public static Sort sort(String... fields) {
    return new Sort(Arrays.asList(fields));
  }

  public List<String> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return String.join(",", fields);
  }

}
