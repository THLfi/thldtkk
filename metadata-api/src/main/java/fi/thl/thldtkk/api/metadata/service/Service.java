package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
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
