package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.UsageCondition;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import fi.thl.thldtkk.api.metadata.service.v3.UsageConditionService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsageConditionServiceImpl implements UsageConditionService {

  private Repository<NodeId, Node> nodes;

  public UsageConditionServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<UsageCondition> findAll() {
    return nodes.query(keyValue("type.id", "UsageCondition"))
        .map(UsageCondition::new)
        .collect(toList());
  }

  @Override
  public List<UsageCondition> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "UsageCondition"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(UsageCondition::new)
        .collect(toList());
  }

  @Override
  public Optional<UsageCondition> get(UUID id) {
    return nodes.get(new NodeId(id, "UsageCondition")).map(UsageCondition::new);
  }

  @Override
  public UsageCondition save(UsageCondition usageCondition) {
    return new UsageCondition(nodes.save(usageCondition.toNode()));
  }

}
