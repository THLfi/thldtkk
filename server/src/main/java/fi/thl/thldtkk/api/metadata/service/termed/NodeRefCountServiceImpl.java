package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.NodeRefCountService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;

public class NodeRefCountServiceImpl implements NodeRefCountService {

  private Repository<NodeId, Node> nodes;

  public NodeRefCountServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public int getReferenceCount(UUID nodeId) {
    int refCount = nodes.query(
      select("id", "referrers.*", "references.*"),
      keyValue("id", nodeId.toString())).findFirst().get().getReferences().size();

    return refCount;
  }

  @Override
  public int getReferrerCount(UUID nodeId) {
    int refCount = nodes.query(
      select("id", "referrers.*", "references.*"),
      keyValue("id", nodeId.toString())).findFirst().get().getReferrers().size();

    return refCount;
  }
}
