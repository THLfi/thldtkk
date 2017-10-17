package fi.thl.thldtkk.api.metadata.test;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class NodeBuilder {
  private UUID id;
  private String type;
  private Multimap<String, StrictLangValue> properties = LinkedHashMultimap.create();
  private Multimap<String, Node> references = LinkedHashMultimap.create();

  public NodeBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public NodeBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public NodeBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public NodeBuilder withProperty(String property, String lang, String value) {
    properties.put(property, new StrictLangValue(lang, value));
    return this;
  }

  public NodeBuilder withReference(String reference, Node node) {
    references.put(reference, node);
    return this;
  }

  public Node build() {
    return new Node(id, type, properties, references);
  }
}
