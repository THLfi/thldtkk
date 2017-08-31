package fi.thl.thldtkk.api.metadata.domain.query;

import java.util.Arrays;
import java.util.List;

public class Select {

  private List<String> fields;

  public Select(List<String> fields) {
    this.fields = fields;
  }

  public static Select select(String... fields) {
    return new Select(Arrays.asList(fields));
  }

  public List<String> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return String.join(",", fields);
  }

}
