package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;

import fi.thl.thldtkk.api.metadata.domain.SystemRole;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.EditorSystemRoleService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorSystemRoleServiceImpl implements EditorSystemRoleService {

  private Repository<NodeId, Node> nodes;

  public EditorSystemRoleServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<SystemRole> findAll() {
    return nodes.query(
        keyValue("type.id", "SystemRole"))
        .map(SystemRole::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<SystemRole> find(String query, int max) {
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", "SystemRole")
        : and(
            keyValue("type.id", "SystemRole"),
            keyWithAnyValue("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
        .map(SystemRole::new)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<SystemRole> get(UUID id) {
    return nodes.get(new NodeId(id, "SystemRole")).map(SystemRole::new);
  }

}