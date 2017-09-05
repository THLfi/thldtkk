package fi.thl.thldtkk.api.metadata.domain.query;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class OrCriteria implements Criteria {

  private List<Criteria> clauses;

  private OrCriteria(List<Criteria> clauses) {
    this.clauses = clauses;
  }

  public static OrCriteria or(Criteria... clauses) {
    return new OrCriteria(Arrays.asList(clauses));
  }

  public static OrCriteria or(List<Criteria> clauses) {
    return new OrCriteria(clauses);
  }

  public List<Criteria> getClauses() {
    return clauses;
  }

  @Override
  public String toString() {
    return "(" + join(" OR ", clauses.stream().map(Object::toString).collect(toList())) + ")";
  }

}