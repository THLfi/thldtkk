package fi.thl.thldtkk.api.metadata.service.v3.termed;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import fi.thl.thldtkk.api.metadata.service.v3.VariableService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

public class VariableServiceImpl implements VariableService {

  private Repository<NodeId, Node> nodes;

  public VariableServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Variable> findAll() {
    return nodes.query(keyValue("type.id", "Variable"))
        .map(Variable::new)
        .collect(toList());
  }

  @Override
  public List<Variable> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "Variable"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Variable::new)
        .collect(toList());
  }

  @Override
  public Optional<Variable> get(UUID id) {
    return nodes.get(new NodeId(id, "Variable")).map(Variable::new);
  }

  @Override
  public Variable save(Variable variable) {
    return new Variable(nodes.save(variable.toNode()));
  }

  @Override
  public void delete(UUID id) {
    nodes.delete(new NodeId(id, "Variable"));
  }

}
