package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.LifecyclePhase;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifecyclePhaseService implements Service<UUID, LifecyclePhase> {

  private Service<NodeId, Node> nodeService;

  @Autowired
  public LifecyclePhaseService(Service<NodeId, Node> nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<LifecyclePhase> query() {
    return nodeService.query("type.id:LifecyclePhase").map(LifecyclePhase::new);
  }

  @Override
  public Stream<LifecyclePhase> query(String query) {
    return query();
  }

  @Override
  public Optional<LifecyclePhase> get(UUID id) {
    return nodeService.get(new NodeId(id, "LifecyclePhase")).map(LifecyclePhase::new);
  }

  @Override
  public LifecyclePhase save(LifecyclePhase value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException();
  }

}
