package fi.thl.thldtkk.api.metadata.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MapUtils {

  private MapUtils() {
  }

  public static <T> BinaryOperator<T> illegalOperator() {
    return (l, r) -> {
      throw new IllegalStateException();
    };
  }

  public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedHashMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends U> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, illegalOperator(), LinkedHashMap::new);
  }

  public static <K, V> Map<K, V> index(Collection<V> collection,
      Function<? super V, ? extends K> keyMapper) {
    return index(collection.stream(), keyMapper);
  }

  public static <K, V> Map<K, V> index(Stream<V> stream,
      Function<? super V, ? extends K> keyMapper) {
    return stream.collect(toLinkedHashMap(keyMapper, Function.identity()));
  }

}
