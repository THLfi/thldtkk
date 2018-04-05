package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorInstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class EditorInstanceVariableServiceImpl implements EditorInstanceVariableService {

  private final Repository<NodeId, Node> nodes;
  private final UserHelper userHelper;

  public EditorInstanceVariableServiceImpl(Repository<NodeId, Node> nodes, UserHelper userHelper) {
    this.nodes = nodes;
    this.userHelper = userHelper;
  }

  @AdminOnly
  @Override
  public List<InstanceVariable> getInstancesVariablesByVariable(UUID variableId, int max) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*", "referrers.*", "referrers.dataSets:2"),
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
    List<InstanceVariable> instanceVariables = nodes.query(
            select("id", "type", "properties.*", "references.*", "referrers.*", "referrers.dataSets:2", "references.ownerOrganization:3"),
            and(keyValue("type.id", InstanceVariable.TERMED_NODE_CLASS),
                    anyKeyWithAllValues(asList(
                            "properties.prefLabel",
                            "properties.description",
                            "properties.technicalName",
                            "properties.freeConcepts",
                            "references.conceptsFromScheme.properties.prefLabel",
                            "references.variable.properties.prefLabel"),
                            tokenizeAndMap(query, t -> t + "*"))),
            max)
            .map(InstanceVariable::new)
            .collect(toList());

    if (!userHelper.isCurrentUserAdmin()) {
      instanceVariables = filterByOrganization(instanceVariables);
    }

    return instanceVariables;
  }

  @Override
  public Optional<InstanceVariable> get(UUID id) {
    throw new UnsupportedOperationException();
  }

  private List<InstanceVariable> filterByOrganization(List<InstanceVariable> instanceVariables) {
    List<String> organizationIds = userHelper.getCurrentUserOrganizations().stream()
            .map(organization -> organization.getId().toString())
            .collect(Collectors.toList());

    instanceVariables.removeIf(instanceVariable -> !organizationIds.contains(instanceVariable.getOwnerOrganizationIdAsString()));
    return instanceVariables;
  }

}
