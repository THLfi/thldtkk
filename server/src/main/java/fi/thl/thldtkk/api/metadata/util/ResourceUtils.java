package fi.thl.thldtkk.api.metadata.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;

public final class ResourceUtils {

  private ResourceUtils() {
  }

  /**
   * Load a class path resource into a string (e.g. read a text file from /src/main/resources)
   */
  public static String resourceToString(String resourceName) {
    try {
      return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
