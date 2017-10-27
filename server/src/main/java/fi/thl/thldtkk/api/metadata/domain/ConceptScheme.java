package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;

public class ConceptScheme {
  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public ConceptScheme(Node node) {
    this.id = node.getId();
    checkArgument(Objects.equals(node.getTypeId(), "ConceptScheme"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }
  
  /** 
   * Constructor for testing purposes
   */
  
  public ConceptScheme(UUID id, Map<String, String> prefLabel) {
    this.id = id;
    this.prefLabel = prefLabel;
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    return new Node(id, "ConceptScheme", props);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    ConceptScheme that = (ConceptScheme) object;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel);
  }
}
