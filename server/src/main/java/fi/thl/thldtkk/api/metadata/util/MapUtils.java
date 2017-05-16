package fi.thl.thldtkk.api.metadata.util;

import java.util.function.BinaryOperator;

public final class MapUtils {

  private MapUtils() {
  }

  public static <T> BinaryOperator<T> illegalOperator() {
    return (l, r) -> {
      throw new IllegalStateException();
    };
  }

}
