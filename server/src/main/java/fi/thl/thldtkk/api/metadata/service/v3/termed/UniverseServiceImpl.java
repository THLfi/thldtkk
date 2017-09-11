package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Universe;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import fi.thl.thldtkk.api.metadata.service.v3.UniverseService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    return nodes.query(
        and(keyValue("type.id", "Universe"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Universe::new)
        .collect(toList());
  }

  @Override
  public Optional<Universe> get(UUID id) {
    return nodes.get(new NodeId(id, "Universe")).map(Universe::new);
  }

  @Override
  public Universe save(Universe universe) {
    return new Universe(nodes.save(universe.toNode()));
  }

}
