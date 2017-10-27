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
