package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.PublicStudyService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api(description = "Editor API for studies, their datasets and datasets' instance variables")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/editor/studies")
public class EditorStudyController {

  @Autowired
  private EditorStudyService editorStudyService;
  @Autowired
  private PublicStudyService publicStudyService;
  @Autowired
  private EditorDatasetService editorDatasetService;

  @ApiOperation("Search studies")
  @GetJsonMapping
  public List<Study> query(
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "max", defaultValue = "-1") int max) {
    return editorStudyService.find(organizationId, query, max, null);
  }

  @ApiOperation("Get one study by ID")
  @GetJsonMapping("/{studyId}")
  public Study getStudy(@PathVariable UUID studyId) {
    return editorStudyService.get(studyId)
      .orElseThrow(entityNotFound(Study.class, studyId));
  }

  @ApiOperation("Create a new study or update an existing study")
  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Study postStudy(@RequestBody @Valid Study study) {
    return editorStudyService.save(study);
  }

  @ApiOperation("Delete an existing study")
  @DeleteMapping("/{studyId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteStudy(@PathVariable UUID studyId) {
    editorStudyService.delete(studyId);
    try {
      publicStudyService.delete(studyId);
    }
    catch (NotFoundException e) {
      // Study hasn't been published, nothing to delete
    }
  }

  @ApiOperation("Get one dataset by study ID and dataset ID")
  @GetJsonMapping("/{studyId}/datasets/{datasetId}")
  public Dataset getDataset(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId) {
    return editorDatasetService.getDatasetWithPredecessorsSuccessors(studyId, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId));
  }

  @ApiOperation("Create a new dataset or update an existing dataset")
  @PostJsonMapping(
    path = "/{studyId}/datasets",
    produces = APPLICATION_JSON_UTF8_VALUE)
  public Dataset postDataset(@PathVariable UUID studyId,
                             @RequestBody @Valid Dataset dataset) {
    return editorStudyService.saveDataset(studyId, dataset);
  }

  @ApiOperation("Delete an existing dataset")
  @DeleteMapping("/{studyId}/datasets/{datasetId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteDataset(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId) {
    editorStudyService.deleteDataset(studyId, datasetId);
  }

  @ApiOperation("Get one instance variable by study ID, dataset ID and instance variable ID")
  @GetJsonMapping(path = "/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  public InstanceVariable getInstanceVariable(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId,
    @PathVariable UUID instanceVariableId) {
    return editorStudyService.getInstanceVariable(studyId, datasetId, instanceVariableId)
            .orElseThrow(entityNotFound(InstanceVariable.class, instanceVariableId));
  }

  @ApiOperation("Create a new instance variable or update an existing instance variable")
  @PostJsonMapping(
    path = "/{studyId}/datasets/{datasetId}/instanceVariables",
    produces = APPLICATION_JSON_UTF8_VALUE)
  public InstanceVariable postInstanceVariable(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId,
    @RequestBody @Valid InstanceVariable instanceVariable) {
    return editorStudyService.saveInstanceVariable(studyId, datasetId, instanceVariable);
  }

  @ApiOperation("Delete an existing instanceVariable")
  @DeleteMapping("/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteInstanceVariable(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId,
    @PathVariable UUID instanceVariableId) {
    editorStudyService.deleteInstanceVariable(studyId, datasetId, instanceVariableId);
  }

  @ApiOperation("Get next instance variable for given one")
  @GetJsonMapping("/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}/next")
  public String getNextInstanceVariable(
          @PathVariable("studyId") UUID studyId,
          @PathVariable("datasetId") UUID datasetId,
          @PathVariable("instanceVariableId") UUID instanceVariableId) {
    return editorStudyService.getNextInstanceVariable(studyId, datasetId, instanceVariableId);
  }
}
