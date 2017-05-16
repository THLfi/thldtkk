package fi.thl.thldtkk.api.metadata.domain.termed;

import com.google.common.base.MoreObjects;
import fi.thl.thldtkk.api.metadata.util.RegularExpressions;
import java.util.Objects;

public class StrictLangValue {

  private String lang;
  private String value;
  private String regex;

  public StrictLangValue(String value) {
    this("", value);
  }

  public StrictLangValue(String lang, String value) {
    this(lang, value, RegularExpressions.ALL);
  }

  public StrictLangValue(String lang, String value, String regex) {
    this.lang = lang;
    this.value = value;
    this.regex = regex;
  }

  public String getLang() {
    return lang;
  }

  public String getValue() {
    return value;
  }

  public String getRegex() {
    return regex;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("lang", lang)
        .add("value", value)
        .add("regex", regex)
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
    StrictLangValue that = (StrictLangValue) o;
    return Objects.equals(lang, that.lang) &&
        Objects.equals(value, that.value) &&
        Objects.equals(regex, that.regex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lang, value, regex);
  }

}
