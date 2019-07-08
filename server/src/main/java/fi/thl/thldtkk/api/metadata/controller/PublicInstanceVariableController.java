package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.PublicInstanceVariableService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.PublicFieldIgnoreUtil.sanitizeInstanceVariable;
import static fi.thl.thldtkk.api.metadata.util.PublicFieldIgnoreUtil.sanitizeInstanceVariableList;

@Api(description = "Public API for instance variables")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/public")
public class PublicInstanceVariableController {

  @Autowired
  private PublicInstanceVariableService instanceVariableService;

  @ApiOperation("List all instance variables of given dataset")
  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getInstanceVariablesOfDataset(@PathVariable("datasetId") UUID id) {
    return sanitizeInstanceVariableList(instanceVariableService.getDatasetInstanceVariables(id));
  }

  @ApiOperation("Get instance variable by ID")
  @GetJsonMapping({
      "/datasets/{datasetId}/instanceVariables/{id}",
      "/instanceVariables/{id}"})
  public InstanceVariable getInstanceVariable(
    @PathVariable("id") UUID id,
    @RequestParam(name = "select", required = false) String selectString
  ) throws IOException {
    Optional<InstanceVariable> variable;
    if (selectString == null) {
      variable = instanceVariableService.get(id);
    } else {
      variable = instanceVariableService.get(id, ControllerUtils.parseSelect(selectString));
    }

    return sanitizeInstanceVariable(variable.orElseThrow(NotFoundException::new));
  }

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getInstancesOfVariable(
      @PathVariable("variableId") UUID variableId) {
    return sanitizeInstanceVariableList(instanceVariableService.getVariableInstancesVariables(variableId, -1));
  }

  @ApiOperation("Search instance variables")
  @GetJsonMapping("/instanceVariables")
  public List<InstanceVariable> getInstanceVariables(
      @RequestParam(value = "query", defaultValue = "") String query,
      @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return sanitizeInstanceVariableList(instanceVariableService.find(query, max));
  }

}
