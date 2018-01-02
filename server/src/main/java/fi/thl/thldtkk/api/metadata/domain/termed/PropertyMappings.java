package fi.thl.thldtkk.api.metadata.domain.termed;

import org.springframework.util.StringUtils;

import static fi.thl.thldtkk.api.metadata.util.RegularExpressions.ALL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class PropertyMappings {

  private PropertyMappings() {
  }

  public static StrictLangValue toPropertyValue(Integer integer) {
    Objects.requireNonNull(integer);
    return new StrictLangValue(String.valueOf(integer));
  }

  public static StrictLangValue toPropertyValue(BigDecimal bigDecimal) {
    Objects.requireNonNull(bigDecimal);
    return new StrictLangValue(String.valueOf(bigDecimal));
  }

  public static StrictLangValue toPropertyValue(String string) {
    Objects.requireNonNull(string);
    return new StrictLangValue(string);
  }

  public static StrictLangValue toPropertyValue(Boolean bool) {
    Objects.requireNonNull(bool);
    return new StrictLangValue("", String.valueOf(bool), "^(true|false)$");
  }

  public static StrictLangValue toPropertyValue(LocalDate date) {

    Objects.requireNonNull(date);

    String formattedDate = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    return new StrictLangValue("", formattedDate, "^\\d{4}-\\d{2}-\\d{2}$");
  }

  public static Collection<StrictLangValue> toPropertyValues(Map<String, String> localizedString) {
    return localizedString.entrySet().stream()
      .map(e -> new StrictLangValue(e.getKey(), e.getValue(), ALL))
      .collect(toList());
  }

  public static Collection<StrictLangValue> toPropertyValues(Collection<String> nonLocalizedStrings) {
    return nonLocalizedStrings.stream()
      .map(string -> new StrictLangValue("", string, ALL))
      .collect(toList());
  }

  public static <T extends Enum<T>> Collection<StrictLangValue> enumsToPropertyValues(Collection<T> enums) {
    return enums.stream()
      .filter(enumValue -> enumValue != null)
      .map(enumValue -> new StrictLangValue("", enumValue.toString(), ALL))
      .collect(toList());
  }

  public static Map<String, String> toLangValueMap(Collection<StrictLangValue> propertyValues) {
    return propertyValues.stream()
      .collect(toMap(StrictLangValue::getLang, StrictLangValue::getValue));
  }

  public static Optional<String> toOptionalString(Collection<StrictLangValue> values) {
    String string = toString(values);
    return StringUtils.hasText(string) ? Optional.of(string) : Optional.empty();
  }

  public static String toString(Collection<StrictLangValue> values) {
    return values.stream()
      .map(StrictLangValue::getValue)
      .collect(Collectors.joining(", "));
  }

  public static Integer toInteger(Collection<StrictLangValue> values, Integer defaultValue) {
    return values.stream()
      .map(StrictLangValue::getValue)
      .findFirst()
      .map(Integer::parseInt)
      .orElse(defaultValue);
  }

  public static Boolean toBoolean(Collection<StrictLangValue> values) {
    return toBoolean(values, null);
  }

  public static Boolean toBoolean(Collection<StrictLangValue> values, Boolean defaultValue) {
    return values.stream()
      .map(StrictLangValue::getValue)
      .findFirst()
      .map(Boolean::valueOf)
      .orElse(defaultValue);
  }

  public static BigDecimal toBigDecimal(Collection<StrictLangValue> values, BigDecimal defaultValue) {
    return values.stream()
      .map(StrictLangValue::getValue)
      .findFirst()
      .map(BigDecimal::new)
      .orElse(defaultValue);
  }

  public static LocalDate toLocalDate(Collection<StrictLangValue> values, LocalDate defaultValue) {
    return values.stream()
      .map(StrictLangValue::getValue)
      .findFirst()
      .map(value -> LocalDate.parse(value))
      .orElse(defaultValue);
  }

  public static <T extends Collection<String>> T valuesToStringCollection(Collection<StrictLangValue> langValues, Supplier<T> collectionSupplier) {
    return langValues.stream()
      .map(langValue -> langValue.getValue())
      .collect(Collectors.toCollection(collectionSupplier));
  }

  public static <T extends Collection<E>, E extends Enum<E>> T valuesToEnumCollection(Collection<StrictLangValue> langValues, Class<E> enumClass, Supplier<T> collectionSupplier) {
    return langValues.stream()
      .map(langValue -> Enum.valueOf(enumClass, langValue.getValue()))
      .collect(Collectors.toCollection(collectionSupplier));
  }

}
