package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.UUID;


public class InstanceQuestion implements NodeEntity{
    
  public static final String TERMED_NODE_CLASS = "InstanceQuestion";

  private UUID id;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  
  public InstanceQuestion(UUID id) {
    this.id = requireNonNull(id);
  }

  public InstanceQuestion(UUID id, Map<String, String> prefLabel) {
    this(id);
    this.prefLabel = prefLabel;
  }

  public InstanceQuestion(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  @Override
  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }
  
  @Override
  public Node toNode() {
    Node node = new Node(id, TERMED_NODE_CLASS);
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    return node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstanceQuestion that = (InstanceQuestion) o;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
  
}