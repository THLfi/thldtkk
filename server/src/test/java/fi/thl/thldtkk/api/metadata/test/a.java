package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.Study;

/**
 * A helper class for tests based on Object Builder pattern.
 */
public class a {

  public static StudyBuilder study() {
    return new StudyBuilder();
  }

  public static DatasetBuilder dataset() {
    return new DatasetBuilder();
  }

  public static PopulationBuilder population() {
    return new PopulationBuilder();
  }

  public static PersonBuilder person() {
    return new PersonBuilder();
  }

  public static PersonInRoleBuilder personInRole() {
    return new PersonInRoleBuilder();
  }

  public static NodeBuilder node() {
    return new NodeBuilder();
  }

  public static NodeIdBuilder nodeId() {
    return new NodeIdBuilder();
  }

  public static NodeBuilder studyNode() {
    return node().withType(Study.TERMED_NODE_CLASS);
  }

  public static NodeIdBuilder studyNodeId() {
    return nodeId().withType(Study.TERMED_NODE_CLASS);
  }

  public static NodeBuilder datasetNode() {
    return node().withType(Dataset.TERMED_NODE_CLASS);
  }

  public static NodeIdBuilder datasetNodeId() {
    return nodeId().withType(Dataset.TERMED_NODE_CLASS);
  }

  public static UnitBuilder unit() {
    return new UnitBuilder();
  }

  public static QuantityBuilder quantity() {
    return new QuantityBuilder();
  }

  public static CodeListBuilder codeList() {
    return new CodeListBuilder();
  }

  public static CodeItemBuilder codeItem() {
    return new CodeItemBuilder();
  }

  public static UnitTypeBuilder unitType() {
    return new UnitTypeBuilder();
  }

  public static VariableBuilder variable() {
    return new VariableBuilder();
  }

  public static ConceptBuilder concept() {
    return new ConceptBuilder();
  }

  public static ConceptSchemeBuilder conceptScheme() {
    return new ConceptSchemeBuilder();
  }


}
