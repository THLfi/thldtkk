package fi.thl.thldtkk.api.metadata.controller;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.domain.query.Sort.sort;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeToStream;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Public API for instance variables")
@RestController
@RequestMapping("/api/v2/public")
public class PublicInstanceVariableController {

  @Autowired
  private Service<NodeId, Node> nodeService;

  @ApiOperation("List all instance variables of given dataset")
  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getDatasetInstanceVariables(@PathVariable("datasetId") UUID id) {
    Dataset dataset = nodeService.query(
        select("id", "type", "properties.*", "references.*"),
        and(keyValue("id", id.toString()),
            keyValue("type.id", "DataSet"),
            keyValue("properties.published", "true")))
        .map(Dataset::new).findAny()
        .orElseThrow(NotFoundException::new);

    return dataset.getInstanceVariables()
        .stream()
        .filter(i -> i.isPublished().orElse(false))
        .collect(Collectors.toList());
  }

  @ApiOperation("Get instance variable by ID")
  @GetJsonMapping({
      "/datasets/{datasetId}/instanceVariables/{id}",
      "/instanceVariables/{id}"})
  public InstanceVariable getInstanceVariable(@PathVariable("id") UUID id) {
    return nodeService.query(
        select("id", "type", "properties.*", "references.*",
            "references.inScheme:2",
            "references.codeItems:2",
            "references.unitType:2"),
        and(keyValue("id", id.toString()),
            keyValue("type.id", "InstanceVariable"),
            keyValue("properties.published", "true")))
        .map(InstanceVariable::new).findAny()
        .orElseThrow(NotFoundException::new);
  }

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getInstancesOfVariable(
      @PathVariable("variableId") UUID variableId) {
    return nodeService.query(
        select("id", "type", "properties.*", "references.*"),
        and(keyValue("type.id", "InstanceVariable"),
            keyValue("properties.published", "true"),
            keyValue("references.variable.id", variableId.toString())),
        sort("properties.prefLabel.sortable"))
        .map(InstanceVariable::new)
        .collect(toList());
  }

  @ApiOperation("List all instance variables")
  @GetJsonMapping("/instanceVariables")
  public List<InstanceVariable> getInstanceVariables(
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "max", required = false, defaultValue = "10") Integer maxResults) {

    List<Criteria> criteria = new ArrayList<>();
    criteria.add(keyValue("type.id", "InstanceVariable"));
    criteria.add(keyValue("properties.published", "true"));

    if (!query.isEmpty()) {
      criteria.add(anyKeyWithAllValues(asList(
          "properties.prefLabel",
          "properties.description",
          "properties.technicalName",
          "properties.freeConcepts",
          "references.conceptsFromScheme.properties.prefLabel",
          "references.variable.properties.prefLabel"),
          tokenizeToStream(query)
              .map(token -> token + "*")
              .collect(toList())));
    }

    return nodeService.query(
        select("id", "type", "properties.*", "references.*", "referrers.instanceVariable"),
        and(criteria),
        sort("properties.prefLabel.sortable"), maxResults)
        .map(InstanceVariable::new)
        .collect(toList());
  }

}
