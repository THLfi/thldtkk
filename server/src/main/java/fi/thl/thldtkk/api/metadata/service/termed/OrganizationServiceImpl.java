package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.stream.Collectors.toList;

public class OrganizationServiceImpl implements OrganizationService {

  private Repository<NodeId, Node> nodes;

  public OrganizationServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Organization> findAll() {
    return nodes.query(keyValue("type.id", "Organization"))
        .map(Organization::new)
        .collect(toList());
  }

  @Override
  public List<Organization> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<Organization> get(UUID id) {
    return nodes.get(new NodeId(id, "Organization")).map(Organization::new);
  }

  @Override
  public Optional<Organization> getByVirtuId(String virtuId) {
    return findAll()
      .stream()
      .filter(org -> org.getVirtuIds().contains(virtuId))
      .findFirst();
  }

  @Override
  public Organization save(Organization organization) {
    return new Organization(nodes.save(organization.toNode()));
  }
}
