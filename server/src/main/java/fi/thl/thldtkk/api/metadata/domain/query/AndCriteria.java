package fi.thl.thldtkk.api.metadata.domain.query;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class AndCriteria implements Criteria {

  private List<Criteria> clauses;

  private AndCriteria(List<Criteria> clauses) {
    this.clauses = clauses;
  }

  public static AndCriteria and(Criteria... clauses) {
    return new AndCriteria(Arrays.asList(clauses));
  }

  public static AndCriteria and(List<Criteria> clauses) {
    return new AndCriteria(clauses);
  }

  public List<Criteria> getClauses() {
    return clauses;
  }

  @Override
  public String toString() {
    return "(" + join(" AND ", clauses.stream().map(Object::toString).collect(toList())) + ")";
  }

}
