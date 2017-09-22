package fi.thl.thldtkk.api.metadata.domain.query;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;

public class OrCriteria implements Criteria {

  private List<Criteria> clauses;

  private OrCriteria(List<Criteria> clauses) {
    this.clauses = clauses;
  }

  public static OrCriteria or(Criteria first, Criteria... rest) {
    return new OrCriteria(Lists.asList(first, rest));
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

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.clauses);
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
    final OrCriteria other = (OrCriteria) obj;
    if (!Objects.equals(this.clauses, other.clauses)) {
      return false;
    }
    return true;
  }

  
  
}
