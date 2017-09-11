package fi.thl.thldtkk.api.metadata.domain.query;

import static fi.thl.thldtkk.api.metadata.domain.query.Select.selectAll;
import static fi.thl.thldtkk.api.metadata.domain.query.Sort.sort;

public class Query {

  private Select select;
  private Criteria where;
  private Sort sort;
  private int max;

  public Query(Select select, Criteria where, Sort sort, int max) {
    this.select = select;
    this.where = where;
    this.sort = sort;
    this.max = max;
  }

  public static Query query(Select select, Criteria where, Sort sort, int max) {
    return new Query(select, where, sort, max);
  }

  public static Query query(Select select, Criteria where, Sort sort) {
    return new Query(select, where, sort, -1);
  }

  public static Query query(Select select, Criteria where) {
    return new Query(select, where, sort(), -1);
  }

  public static Query query(Criteria where, int max) {
    return new Query(selectAll(), where, sort(), max);
  }

  public static Query query(Criteria where) {
    return new Query(selectAll(), where, sort(), -1);
  }

  public Select getSelect() {
    return select;
  }

  public Criteria getWhere() {
    return where;
  }

  public Sort getSort() {
    return sort;
  }

  public int getMax() {
    return max;
  }

}
