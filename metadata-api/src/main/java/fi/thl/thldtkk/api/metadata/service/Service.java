package fi.thl.thldtkk.api.metadata.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface Service<T> {

  Stream<T> query();

  Optional<T> get(UUID id);

  T save(T value);

  void delete(UUID id);

}
