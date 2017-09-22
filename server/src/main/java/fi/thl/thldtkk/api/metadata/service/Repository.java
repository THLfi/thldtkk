package fi.thl.thldtkk.api.metadata.service;

import static fi.thl.thldtkk.api.metadata.domain.query.Select.selectAll;

import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Query;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface meant for lower level CRUD service
 */
public interface Repository<K, V> {

  /**
   * Find all values
   *
   * @return all values
   */
  Stream<V> query();

  default Stream<V> query(Criteria criteria) {
    return query(Query.query(criteria));
  }

  default Stream<V> query(Criteria criteria, Sort sort) {
    return query(Query.query(criteria, sort));
  }

  default Stream<V> query(Criteria criteria, int max) {
    return query(Query.query(criteria, max));
  }

  default Stream<V> query(Criteria criteria, int max, Sort sort) {
    return query(Query.query(criteria, max, sort));
  }

  default Stream<V> query(Select select, Criteria criteria) {
    return query(Query.query(select, criteria));
  }

  default Stream<V> query(Select select, Criteria criteria, int max) {
    return query(Query.query(select, criteria, max));
  }

  default Stream<V> query(Select select, Criteria criteria, Sort sort) {
    return query(Query.query(select, criteria, sort));
  }

  default Stream<V> query(Query query) {
    return query(query.getSelect(), query.getWhere(), query.getSort(), query.getMax());
  }

  /**
   * Find max number of values by given criteria and ordered by given fields
   */
  Stream<V> query(Select select, Criteria criteria, Sort sort, int max);

  /**
   * Find one value by id
   *
   * @param id of the requested value
   * @return value or empty optional
   */
  default Optional<V> get(K id) {
    return get(selectAll(), id);
  }

  /**
   * Find one value by id
   *
   * @param select describing what fields should be returned
   * @param id of the requested value
   * @return value or empty optional
   */
  Optional<V> get(Select select, K id);

  /**
   * Save multiple values
   *
   * @param values to be saved
   */
  default void save(List<V> values) {
    values.forEach(this::save);
  }

  /**
   * Save one value
   *
   * @param value to be saved
   * @return saved value
   */
  default V save(V value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Delete multiple values by id list
   *
   * @param ids of values to be deleted
   */
  default void delete(List<K> ids) {
    ids.forEach(this::delete);
  }

  /**
   * Delete one value by id
   *
   * @param id of value to be deleted
   */
  default void delete(K id) {
    throw new UnsupportedOperationException();
  }

  /**
   * Post multiple deletes and saves in one batch
   *
   * @param changeset containing value deletes and saves
   */
  default void post(Changeset<K, V> changeset) {
    delete(changeset.getDelete());
    save(changeset.getSave());
  }

}
