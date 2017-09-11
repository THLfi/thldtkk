package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.v3.InstanceVariableService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Public API for instance variables")
@RestController
@RequestMapping("/api/v3/public")
public class PublicInstanceVariableController3 {

  @Autowired
  @Qualifier("publicInstanceVariableService")
  private InstanceVariableService instanceVariableService;

  @ApiOperation("List all instance variables of given dataset")
  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getInstanceVariablesOfDataset(@PathVariable("datasetId") UUID id) {
    return instanceVariableService.getDatasetInstanceVariables(id);
  }

  @ApiOperation("Get instance variable by ID")
  @GetJsonMapping({
      "/datasets/{datasetId}/instanceVariables/{id}",
      "/instanceVariables/{id}"})
  public InstanceVariable getInstanceVariable(@PathVariable("id") UUID id) {
    return instanceVariableService.get(id).orElseThrow(NotFoundException::new);
  }

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getInstancesOfVariable(
      @PathVariable("variableId") UUID variableId) {
    return instanceVariableService.getVariableInstancesVariables(variableId);
  }

  @ApiOperation("List all instance variables")
  @GetJsonMapping("/instanceVariables")
  public List<InstanceVariable> getInstanceVariables(
      @RequestParam(value = "query", defaultValue = "") String query,
      @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return instanceVariableService.find(query, max);
  }

}
