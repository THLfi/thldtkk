package fi.thl.thldtkk.api.metadata.util;

import java.util.Arrays;
import java.util.List;
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
   * Split input to tokens by any non word character
   */
  public static Stream<String> tokenizeToStream(String input) {
    return Arrays.stream(NON_WORD_CHAR.split(input));
  }

}
