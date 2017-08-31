package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Query;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Generic CRUD interface for Services
 *
 * @param <K> key type of stored values
 * @param <V> value type of stored values
 */
public interface Service<K, V> {

  /**
   * Find all values
   *
   * @return all values
   */
  Stream<V> query();

  /**
   * Find values by query
   *
   * @param query to search with (actual query format is service dependent)
   * @return matching values
   */
  Stream<V> query(String query);

  /**
   * Find values by query object
   *
   * @param query to search with
   * @return matching values
   */
  default Stream<V> query(Query query) {
    return query(query.toString());
  }

  default Stream<V> query(Select select, Criteria criteria) {
    return query(Query.query(select, criteria));
  }

  default Stream<V> query(Select select, Criteria criteria, Sort sort) {
    return query(Query.query(select, criteria, sort));
  }

  default Stream<V> query(Select select, Criteria criteria, Sort sort, int max) {
    return query(Query.query(select, criteria, sort, max));
  }

  /**
   * Find one value by id
   *
   * @param id of the requested value
   * @return value or empty optional
   */
  Optional<V> get(K id);

  /**
   * Find one value by id
   *
   * @param id of the requested value
   * @param select describing what fields should be returned
   * @return value or empty optional
   */
  default Optional<V> get(K id, String select) {
    return get(id);
  }

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
  V save(V value);

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
  void delete(K id);

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
