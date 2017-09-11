package fi.thl.thldtkk.api.metadata.util;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Tokenizer {

  private static Pattern NON_WORD_CHAR = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

  private Tokenizer() {
  }

  /**
   * Split input to tokens by any non word character
   */
  public static List<String> tokenize(String input) {
    return Arrays.asList(NON_WORD_CHAR.split(input));
  }

  /**
   * Split input to tokens by any non word character and map with given function
   */
  public static List<String> tokenizeAndMap(String input, Function<String, String> mapping) {
    return tokenizeToStream(input).map(mapping).collect(toList());
  }

  /**
   * Split input to tokens by any non word character
   */
  public static Stream<String> tokenizeToStream(String input) {
    return Arrays.stream(NON_WORD_CHAR.split(input));
  }

}
