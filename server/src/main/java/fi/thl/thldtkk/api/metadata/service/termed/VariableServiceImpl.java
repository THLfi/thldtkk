package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.VariableService;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.Constants.DEFAULT_LANG;
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
    return nodes.query(keyValue("type.id", Variable.TERMED_NODE_CLASS))
        .map(Variable::new)
        .collect(toList());
  }

  @Override
  public List<Variable> find(String query, int max) {
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", Variable.TERMED_NODE_CLASS)
        : and(
            keyValue("type.id", Variable.TERMED_NODE_CLASS),
            keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
        .map(Variable::new)
        .collect(toList());
  }

  @Override
  public Optional<Variable> get(UUID id) {
    return nodes.get(new NodeId(id, Variable.TERMED_NODE_CLASS)).map(Variable::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Variable save(@P("entity") Variable variable) {
    return new Variable(nodes.save(variable.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    nodes.delete(new NodeId(id, Variable.TERMED_NODE_CLASS), true);
  }

  @Override
  public Optional<Variable> findByPrefLabel(String prefLabel) {
    if (prefLabel == null || prefLabel.isEmpty()) {
      return Optional.empty();
    }

    return nodes.query(
      and(
        keyValue("type.id", Variable.TERMED_NODE_CLASS),
        keyValue("properties.prefLabel", "\"" + prefLabel + "\"")
      ))
      .map(Variable::new)
      .filter(concept -> prefLabel.equalsIgnoreCase(concept.getPrefLabel().get(DEFAULT_LANG)))
      .findFirst();
  }

}
