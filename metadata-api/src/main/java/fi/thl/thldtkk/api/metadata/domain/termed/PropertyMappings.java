package fi.thl.thldtkk.api.metadata.domain.termed;

import static fi.thl.thldtkk.api.metadata.util.RegularExpressions.ALL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class PropertyMappings {

  private PropertyMappings() {
  }

  public static Collection<StrictLangValue> toPropertyValues(Integer integer) {
    return Collections.singleton(new StrictLangValue(String.valueOf(integer)));
  }

  public static Collection<StrictLangValue> toPropertyValues(String string) {
    return Collections.singleton(new StrictLangValue(string));
  }

  public static Collection<StrictLangValue> toPropertyValues(Map<String, String> localizedString) {
    return localizedString.entrySet().stream()
        .map(e -> new StrictLangValue(e.getKey(), e.getValue(), ALL))
        .collect(toList());
  }

  public static Map<String, String> toLangValueMap(Collection<StrictLangValue> propertyValues) {
    return propertyValues.stream()
        .collect(toMap(StrictLangValue::getLang, StrictLangValue::getValue));
  }

  public static String toSingleString(Collection<StrictLangValue> values) {
    return values.stream()
        .map(StrictLangValue::getValue)
        .collect(Collectors.joining(", "));
  }

  public static Integer toSingleInteger(Collection<StrictLangValue> values, Integer defaultValue) {
    return values.stream()
        .map(StrictLangValue::getValue)
        .findFirst()
        .map(Integer::parseInt)
        .orElse(defaultValue);
  }

}
