package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.UnitService;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnitServiceImpl implements UnitService {

  private Repository<NodeId, Node> nodes;

  public UnitServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Unit> findAll() {
    return nodes.query(keyValue("type.id", "Unit"))
        .map(Unit::new)
        .collect(toList());
  }

  @Override
  public List<Unit> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "Unit"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Unit::new)
        .collect(toList());
  }

  @Override
  public List<Unit> findBySymbol(String symbol) {
    return nodes.query(
        and(keyValue("type.id", "Unit"),
            keyValue("properties.symbol", "\"" + symbol + "\"")))
        .map(Unit::new)
        .collect(toList());
  }

  @Override
  public Optional<Unit> get(UUID id) {
    return nodes.get(new NodeId(id, "Unit")).map(Unit::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Unit save(@P("entity") Unit unit) {
    return new Unit(nodes.save(unit.toNode()));
  }

}
