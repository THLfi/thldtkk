package fi.thl.thldtkk.api.metadata.service.v3;

import java.util.List;
import java.util.Optional;

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
  List<V> findAll();

  /**
   * Find values by query string
   *
   * @param query to search with
   * @param max results list size
   * @return matching values
   */
  List<V> find(String query, int max);

  /**
   * Find one value by id
   *
   * @param id of the requested value
   * @return value or empty optional
   */
  Optional<V> get(K id);

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

}
