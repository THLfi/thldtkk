package fi.thl.thldtkk.api.metadata.service;

import java.util.UUID;


public interface NodeRefCountService {
  int getReferenceCount(UUID nodeId);

  int getReferrerCount(UUID nodeId);
}
