package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;

import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class NodeIdBuilder {
  private UUID id;
  private String type;

  public NodeIdBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public NodeIdBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public NodeIdBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public NodeId build() {
    return new NodeId(id, type);
  }
}
