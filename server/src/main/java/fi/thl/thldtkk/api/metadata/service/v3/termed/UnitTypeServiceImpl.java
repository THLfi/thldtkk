package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import fi.thl.thldtkk.api.metadata.service.v3.UnitTypeService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnitTypeServiceImpl implements UnitTypeService {

  private Repository<NodeId, Node> nodes;

  public UnitTypeServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<UnitType> findAll() {
    return nodes.query(keyValue("type.id", "UnitType"))
        .map(UnitType::new)
        .collect(toList());
  }

  @Override
  public List<UnitType> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "UnitType"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(UnitType::new)
        .collect(toList());
  }

  @Override
  public Optional<UnitType> get(UUID id) {
    return nodes.get(new NodeId(id, "UnitType")).map(UnitType::new);
  }

  @Override
  public UnitType save(UnitType unitType) {
    return new UnitType(nodes.save(unitType.toNode()));
  }

}
