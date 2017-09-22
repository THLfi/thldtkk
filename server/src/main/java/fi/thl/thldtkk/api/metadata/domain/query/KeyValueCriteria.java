package fi.thl.thldtkk.api.metadata.domain.query;

import java.util.Objects;

public class KeyValueCriteria implements Criteria {

  private String key;
  private String value;

  public KeyValueCriteria(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + Objects.hashCode(this.key);
    hash = 43 * hash + Objects.hashCode(this.value);
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
    final KeyValueCriteria other = (KeyValueCriteria) obj;
    if (!Objects.equals(this.key, other.key)) {
      return false;
    }
    if (!Objects.equals(this.value, other.value)) {
      return false;
    }
    return true;
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
