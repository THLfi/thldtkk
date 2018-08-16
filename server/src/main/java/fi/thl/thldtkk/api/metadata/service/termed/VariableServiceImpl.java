package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.VariableService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.method.P;

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
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", "Variable")
        : and(
            keyValue("type.id", "Variable"),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
        .map(Variable::new)
        .collect(toList());
  }

  @Override
  public Optional<Variable> get(UUID id) {
    return nodes.get(new NodeId(id, "Variable")).map(Variable::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Variable save(@P("entity") Variable variable) {
    return new Variable(nodes.save(variable.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    nodes.delete(new NodeId(id, "Variable"));
  }

  @Override
  public Optional<Variable> findByPrefLabel(String prefLabel) {
    if (prefLabel == null || prefLabel.isEmpty()) {
      return Optional.empty();
    }

    prefLabel = "\"" + prefLabel + "\"";

    return nodes.query(
        KeyValueCriteria.keyValue(
            "properties.prefLabel",
            prefLabel),
        1)
        .map(Variable::new)
        .findFirst();
  }
}
