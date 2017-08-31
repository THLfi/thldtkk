package fi.thl.thldtkk.api.metadata.domain.query;

public class KeyValueCriteria implements Criteria {

  private String key;
  private String value;

  public KeyValueCriteria(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public static KeyValueCriteria keyValue(String key, String value) {
    return new KeyValueCriteria(key, value);
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return key + ":" + value;
  }

}
