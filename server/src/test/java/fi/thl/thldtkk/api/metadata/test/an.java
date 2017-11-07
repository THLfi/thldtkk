package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;

/**
 * A helper class for tests based Object Builder pattern.
 * See e.g. http://blog.haystacktrace.com/test-object-builders-for-unit-testing/ for more.
 */

public class an {
  public static OrganizationBuilder organization() {
    return new OrganizationBuilder();
  }

  public static NodeBuilder organizationNode() {
    return a.node().withType("Organization");
  }

  public static InstanceVariableBuilder instanceVariable() {
    return new InstanceVariableBuilder();
  }

  public static NodeBuilder instanceVariableNode() {
    return a.node().withType(InstanceVariable.TERMED_NODE_CLASS);
  }

  public static NodeIdBuilder instanceVariableNodeId() {
    return a.nodeId().withType(InstanceVariable.TERMED_NODE_CLASS);
  }

  public static InstanceQuestionBuilder instanceQuestion() {
    return new InstanceQuestionBuilder();
  }
}
