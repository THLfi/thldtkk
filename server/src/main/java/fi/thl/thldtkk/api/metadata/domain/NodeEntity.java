package fi.thl.thldtkk.api.metadata.domain;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;

import java.util.UUID;

public interface NodeEntity {
  UUID getId();
  Node toNode();
}
