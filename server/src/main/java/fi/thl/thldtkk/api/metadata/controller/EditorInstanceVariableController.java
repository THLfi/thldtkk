package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.EditorInstanceVariableService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api(description = "Editor API for instance variables")
@RestController
@RequestMapping("/api/v3/editor")
public class EditorInstanceVariableController {

  @Autowired
  private EditorInstanceVariableService instanceVariableService;

  @Autowired
  private EditorDatasetService editorDatasetService;

  @ApiOperation("Get instance variable by ID")
  @GetJsonMapping({
      "/datasets/{datasetId}/instanceVariables/{id}",
      "/instanceVariables/{id}"})
  public InstanceVariable getInstanceVariable(@PathVariable("id") UUID id) {
    return instanceVariableService.get(id).orElseThrow(NotFoundException::new);
  }

  @ApiOperation("List all instance variables of given dataset")
  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getInstanceVariablesOfDataset(@PathVariable("datasetId") UUID id) {
    return instanceVariableService.getDatasetInstanceVariables(id);
  }

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getVariableInstanceVariables(
      @PathVariable("variableId") UUID variableId) {
    return instanceVariableService.getVariableInstancesVariables(variableId, -1);
  }

  @ApiOperation("List all instance variables of given code list")
  @GetJsonMapping("/codeLists/{codeListId}/instanceVariables")
  public List<InstanceVariable> getCodeListInstanceVariables(
          @PathVariable("codeListId") UUID codeListId) {
    return instanceVariableService.getInstanceVariablesByCodeList(codeListId);
  }

  @PostJsonMapping(path = "/datasets/{datasetId}/instanceVariables",
      produces = APPLICATION_JSON_UTF8_VALUE)
  public InstanceVariable saveDatasetInstanceVariable(@PathVariable("datasetId") UUID datasetId,
      @RequestBody @Valid InstanceVariable instanceVariable) {
    return editorDatasetService.saveDatasetInstanceVariable(datasetId, instanceVariable);
  }

  @DeleteMapping("/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteDatasetInstanceVariable(
      @PathVariable("datasetId") UUID datasetId,
      @PathVariable("instanceVariableId") UUID instanceVariableId) {
    editorDatasetService.deleteDatasetInstanceVariable(datasetId, instanceVariableId);
  }


}
