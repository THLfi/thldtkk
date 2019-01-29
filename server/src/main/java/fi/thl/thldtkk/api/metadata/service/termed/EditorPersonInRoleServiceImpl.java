package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorPersonInRoleService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;

public class EditorPersonInRoleServiceImpl implements EditorPersonInRoleService {

  private Repository<NodeId, Node> nodes;

  public EditorPersonInRoleServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @AdminOnly
  @Override
  public List<PersonInRole> getPersonInRoles(UUID personId) {
    return nodes.query(and(
      keyValue("type.id", "PersonInRole"),
      keyValue("references.person.id", personId.toString())
      ))
      .map(PersonInRole::new)
      .collect(Collectors.toList());
  }

  @Override
  public List<PersonInRole> findAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<PersonInRole> find(String query, int max) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<PersonInRole> get(UUID id) {
    throw new UnsupportedOperationException();
  }

  @AdminOnly
  @Override
  public void deletePersonInRoles(UUID personId) {
    getPersonInRoles(personId).stream()
      .map(personInRole -> personInRole.getId())
      .forEach(nodeId -> nodes.delete(new NodeId(nodeId, "PersonInRole"), true));
  }
}
