package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.PublicStudyService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;

@Api(description = "Public API for studies, their datasets and datasets' instance variables")
@RestController
@RequestMapping("/api/v3/public/studies")
public class PublicStudyController {

  @Autowired
  private PublicStudyService publicStudyService;

  @ApiOperation("Search studies")
  @GetJsonMapping
  public List<Study> query(
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "max", defaultValue = "-1") int max) {
    return publicStudyService.find(organizationId, query, max, null);
  }

  @ApiOperation("Get one study by ID")
  @GetJsonMapping("/{studyId}")
  public Study getStudy(@PathVariable UUID studyId) {
    return publicStudyService.get(studyId)
      .orElseThrow(entityNotFound(Study.class, studyId));
  }

  @ApiOperation("Get one dataset by study ID and dataset ID")
  @GetJsonMapping("/{studyId}/datasets/{datasetId}")
  public Dataset getDataset(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId) {
    return publicStudyService.getDataset(studyId, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId));
  }

  @ApiOperation("Get one instance variable by study ID, dataset ID and instance variable ID")
  @GetJsonMapping(path = "/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  public InstanceVariable getInstanceVariable(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId,
    @PathVariable UUID instanceVariableId) {
    return publicStudyService.getInstanceVariable(studyId, datasetId, instanceVariableId)
            .orElseThrow(entityNotFound(InstanceVariable.class, instanceVariableId));
  }

}
