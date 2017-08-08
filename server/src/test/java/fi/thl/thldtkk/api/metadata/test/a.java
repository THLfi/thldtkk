package fi.thl.thldtkk.api.metadata.test;

/**
 * A helper class for tests based Object Builder pattern.
 * See e.g. http://blog.haystacktrace.com/test-object-builders-for-unit-testing/ for more.
 */
public class a {

  public static DatasetBuilder dataset() {
    return new DatasetBuilder();
  }

  public static PopulationBuilder population() {
    return new PopulationBuilder();
  }

  public static NodeBuilder node() {
    return new NodeBuilder();
  }

  public static NodeBuilder datasetNode() {
    return node().withType("DataSet");
  }

}
