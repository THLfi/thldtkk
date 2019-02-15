package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.UnitTypeService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

public class UnitTypeServiceImpl implements UnitTypeService {

  private final Repository<NodeId, Node> nodes;

  public UnitTypeServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<UnitType> findAll() {
    return nodes.query(keyValue("type.id", UnitType.TERMED_NODE_CLASS))
        .map(UnitType::new)
        .collect(toList());
  }

  @Override
  public List<UnitType> find(String query, int max) {
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", UnitType.TERMED_NODE_CLASS)
        : and(
            keyValue("type.id", UnitType.TERMED_NODE_CLASS),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
        .map(UnitType::new)
        .collect(toList());
  }

  @Override
  public Optional<UnitType> get(UUID id) {
    return nodes.get(new NodeId(id, UnitType.TERMED_NODE_CLASS)).map(UnitType::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public UnitType save(@P("entity") UnitType unitType) {
    return new UnitType(nodes.save(unitType.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    UnitType unitType = get(id).orElseThrow(NotFoundException::new);
    nodes.delete(new NodeId(unitType.toNode()), true);
  }

  @Override
  public Optional<UnitType> findByPrefLabel(String prefLabel) {
    if (prefLabel == null || prefLabel.isEmpty()) {
      return Optional.empty();
    }

    prefLabel = "\"" + prefLabel + "\"";

    return nodes.query(
      and(
        keyValue("type.id", UnitType.TERMED_NODE_CLASS),
        keyValue("properties.prefLabel", prefLabel)
      ),
      1)
      .map(UnitType::new)
      .findFirst();
  }

}
