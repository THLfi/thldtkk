package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.LifecyclePhase;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.LifecyclePhaseService;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LifecyclePhaseServiceImpl implements LifecyclePhaseService {

  private Repository<NodeId, Node> nodes;

  public LifecyclePhaseServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<LifecyclePhase> findAll() {
    return nodes.query(keyValue("type.id", "LifecyclePhase"))
        .map(LifecyclePhase::new)
        .collect(toList());
  }

  @Override
  public List<LifecyclePhase> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<LifecyclePhase> get(UUID id) {
    return nodes.get(new NodeId(id, "LifecyclePhase")).map(LifecyclePhase::new);
  }

}
