package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class InstanceVariableService {

  private Service<NodeId, Node> nodeService;

  @Autowired
  public InstanceVariableService(Service<NodeId, Node> nodeService) {
    this.nodeService = nodeService;
  }

  public Optional<InstanceVariable> get(UUID id) {
    return nodeService.get(new NodeId(id, "InstanceVariable"), "*,references.inScheme:2").map(InstanceVariable::new);
  }

}
