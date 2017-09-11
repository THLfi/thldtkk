package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.OrganizationUnitService;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrganizationUnitServiceImpl implements OrganizationUnitService {

  private Repository<NodeId, Node> nodes;

  public OrganizationUnitServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<OrganizationUnit> findAll() {
    return nodes.query(keyValue("type.id", "OrganizationUnit"))
        .map(OrganizationUnit::new)
        .collect(toList());
  }

  @Override
  public List<OrganizationUnit> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<OrganizationUnit> get(UUID id) {
    return nodes.get(new NodeId(id, "OrganizationUnit")).map(OrganizationUnit::new);
  }

}
