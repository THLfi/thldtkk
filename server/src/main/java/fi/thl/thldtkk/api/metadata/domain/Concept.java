package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;

public class Concept {

  public static final String TERMED_NODE_CLASS = "Concept";

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private ConceptScheme conceptScheme;

  /**
   * Required by GSON deserialization.
   */
  private Concept() {

  }

  public Concept(Node node) {
    this.id = node.getId();
    checkArgument(Objects.equals(node.getTypeId(), "Concept"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    node.getReferencesFirst("inScheme")
      .ifPresent(cs -> this.conceptScheme = new ConceptScheme(cs));
  }

  /**
   * Constructor for testing purposes
   */
  public Concept(UUID id, Map<String, String> prefLabel, ConceptScheme conceptScheme) {
    this.id = id;
    this.prefLabel = prefLabel;
    this.conceptScheme = conceptScheme;
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Optional<ConceptScheme> getConceptScheme() {
    return Optional.ofNullable(conceptScheme);
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getConceptScheme().ifPresent(cs -> refs.put("inScheme", cs.toNode()));

    return new Node(id, "Concept", props, refs);
  }


  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || getClass() != object.getClass()) {
      return false;
    }

    Concept that = (Concept) object;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel)
      && Objects.equals(conceptScheme, that.conceptScheme);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, conceptScheme);
  }
}
