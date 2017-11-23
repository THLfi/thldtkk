package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorInstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static java.util.stream.Collectors.toList;

public class EditorInstanceVariableServiceImpl implements EditorInstanceVariableService {

  private final Repository<NodeId, Node> nodes;

  public EditorInstanceVariableServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @AdminOnly
  @Override
  public List<InstanceVariable> getInstancesVariablesByVariable(UUID variableId, int max) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*", "referrers.*"),
      and(
        keyValue("type.id", InstanceVariable.TERMED_NODE_CLASS),
        keyValue("references.variable.id", variableId.toString())
      ),
      max)
      .map(InstanceVariable::new)
      .collect(toList());
  }

  @AdminOnly
  @Override
  public List<InstanceVariable> getInstanceVariablesByUnitType(UUID unitTypeId) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*"),
      and(
        keyValue("type.id", InstanceVariable.TERMED_NODE_CLASS),
        keyValue("references.unitType.id", unitTypeId.toString())))
      .map(InstanceVariable::new)
      .collect(Collectors.toList());
  }

  @AdminOnly
  @Override
  public List<InstanceVariable> getInstanceVariablesByCodeList(UUID codeListId) {
    return nodes.query(
            select("id", "type", "properties.*", "references.*"),
            and(
                    keyValue("type.id", InstanceVariable.TERMED_NODE_CLASS),
                    keyValue("references.codeList.id", codeListId.toString())))
            .map(InstanceVariable::new)
            .collect(Collectors.toList());
  }

  // Following methods throw exception on purpose. Instance variables search
  // is not currently needed in editor API.
  // If you need to implement these remember to implement dataset organization
  // check as well.

  @Override
  public List<InstanceVariable> findAll() {
    return find(null, -1);
  }

  @Override
  public List<InstanceVariable> find(String query, int max) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<InstanceVariable> get(UUID id) {
    throw new UnsupportedOperationException();
  }

}
