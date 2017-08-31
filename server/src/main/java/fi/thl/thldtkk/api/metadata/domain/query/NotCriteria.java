package fi.thl.thldtkk.api.metadata.domain.query;

public class NotCriteria implements Criteria {

  private Criteria criteria;

  public NotCriteria(Criteria criteria) {
    this.criteria = criteria;
  }

  public static NotCriteria not(Criteria criteria) {
    return new NotCriteria(criteria);
  }

  public Criteria getCriteria() {
    return criteria;
  }

  @Override
  public String toString() {
    return "NOT " + criteria;
  }

}
