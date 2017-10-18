package fi.thl.thldtkk.api.metadata.test;

/**
 * A helper class for tests based Object Builder pattern.
 * See e.g. http://blog.haystacktrace.com/test-object-builders-for-unit-testing/ for more.
 */

public class an {
  public static OrganizationBuilder organization() {
    return new OrganizationBuilder();
  }

  public static NodeBuilder organizationNode() {
    return new NodeBuilder().withType("Organization");
  }
}
