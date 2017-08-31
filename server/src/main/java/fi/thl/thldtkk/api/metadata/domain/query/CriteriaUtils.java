package fi.thl.thldtkk.api.metadata.domain.query;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.OrCriteria.or;

import java.util.ArrayList;
import java.util.List;

public final class CriteriaUtils {

  private CriteriaUtils() {
  }

  /**
   * @return criteria of ((key_0:value_0 AND key_0:value_1 ...) OR (key_1:value_0 AND key_1:value_1
   * ...) OR ...)
   */
  public static OrCriteria anyKeyWithAllValues(List<String> keys, List<String> values) {
    List<Criteria> clauses = new ArrayList<>();
    keys.forEach(key -> clauses.add(keyWithAllValues(key, values)));
    return or(clauses);
  }

  /**
   * @return criteria of (key:value_0 AND key:value_1 AND key:value_2 ...)
   */
  public static AndCriteria keyWithAllValues(String key, List<String> values) {
    List<Criteria> keyValues = new ArrayList<>();
    values.forEach(value -> keyValues.add(new KeyValueCriteria(key, value)));
    return and(keyValues);
  }

  /**
   * @return criteria of (key:value_0 OR key:value_1 OR key:value_2 ...)
   */
  public static OrCriteria keyWithAnyValue(String key, List<String> values) {
    List<Criteria> keyValues = new ArrayList<>();
    values.forEach(value -> keyValues.add(new KeyValueCriteria(key, value)));
    return or(keyValues);
  }

}
