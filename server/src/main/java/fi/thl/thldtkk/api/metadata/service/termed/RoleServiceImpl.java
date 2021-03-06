package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Role;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.RoleService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.method.P;

public class RoleServiceImpl implements RoleService {

  private Repository<NodeId, Node> nodes;

  public RoleServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Role> findAll() {
    return nodes.query(keyValue("type.id", "Role"))
        .map(Role::new)
        .collect(toList());
  }

  @Override
  public List<Role> find(String query, int max) {
    Criteria criteria = query.isEmpty()
      ? keyValue("type.id", "Role")
      : and(
          keyValue("type.id", "Role"),
          keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max).map(Role::new).collect(toList());
  }

  @Override
  public Optional<Role> get(UUID id) {
    return nodes.get(new NodeId(id, "Role")).map(Role::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Role save(@P("entity") Role role) {
    return new Role(nodes.save(role.toNode()));
  }

}
