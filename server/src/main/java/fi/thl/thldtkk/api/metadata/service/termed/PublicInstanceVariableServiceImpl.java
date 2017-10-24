package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.query.OrCriteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;
import fi.thl.thldtkk.api.metadata.service.PublicInstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class PublicInstanceVariableServiceImpl implements PublicInstanceVariableService {

  private final Repository<NodeId, Node> nodes;
  private final PublicDatasetService datasetService;

  public PublicInstanceVariableServiceImpl(Repository<NodeId, Node> nodes,
                                           PublicDatasetService datasetService) {
    this.nodes = nodes;
    this.datasetService = datasetService;
  }

  @Override
  public List<InstanceVariable> getDatasetInstanceVariables(UUID datasetId) {
    Dataset dataset = datasetService.getDatasetForInstanceVariables(datasetId)
      .orElseThrow(NotFoundException::new);
    return dataset.getInstanceVariables();
  }

  @Override
  public List<InstanceVariable> getVariableInstancesVariables(UUID variableId, int max) {
    return nodes.query(
        select("id", "type", "properties.*", "references.*", "referrers.*"),
        and(keyValue("type.id", "InstanceVariable"),
            keyValue("references.variable.id", variableId.toString())),
        max)
        .map(InstanceVariable::new)
        .collect(toList());
  }

  @Override
  public List<InstanceVariable> findAll() {
    return nodes.query(
        keyValue("type.id", "InstanceVariable"))
        .map(InstanceVariable::new)
        .collect(toList());
  }

  @Override
  public List<InstanceVariable> find(String query, int max) {
    return nodes.query(
        select("id", "type", "properties.*", "references.*", "referrers.*"),
        and(keyValue("type.id", "InstanceVariable"),
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
  }

  @Override
  public Optional<InstanceVariable> get(UUID id) {
    return nodes.get(select("id", "type", "properties.*", "references.*",
        "references.inScheme:2",
        "references.codeItems:2",
        "references.unitType:2"),
        new NodeId(id, "InstanceVariable")).map(InstanceVariable::new);
  }

}
