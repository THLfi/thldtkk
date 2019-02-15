package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Universe;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.UniverseService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.method.P;

public class UniverseServiceImpl implements UniverseService {

  private Repository<NodeId, Node> nodes;

  public UniverseServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Universe> findAll() {
    return nodes.query(keyValue("type.id", "Universe"))
        .map(Universe::new)
        .collect(toList());
  }

  @Override
  public List<Universe> find(String query, int max) {
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", "Universe")
        : and(
            keyValue("type.id", "Universe"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
        .map(Universe::new)
        .collect(toList());
  }

  @Override
  public Optional<Universe> get(UUID id) {
    return nodes.get(new NodeId(id, "Universe")).map(Universe::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Universe save(@P("entity") Universe universe) {
    return new Universe(nodes.save(universe.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    Universe universe = get(id).orElseThrow(NotFoundException::new);
    nodes.delete(new NodeId(universe.toNode()), true);
  }
}
