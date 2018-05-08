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

import static fi.thl.thldtkk.api.metadata.util.PublicFieldIgnoreUtil.*;
import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;

@Api(description = "Public API for studies, their datasets and datasets' instance variables")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/public")
public class PublicStudyController {

  @Autowired
  private PublicStudyService publicStudyService;

  @ApiOperation("Search studies")
  @GetJsonMapping("/studies")
  public List<Study> query(
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "max", defaultValue = "-1") int max,
      @RequestParam(name = "sort", defaultValue = "") String sort) {
    return sanitizeStudyList(publicStudyService.find(organizationId, query, max, sort));
  }

  @ApiOperation("Get one study by ID")
  @GetJsonMapping("/studies/{studyId}")
  public Study getStudy(@PathVariable UUID studyId) {
    return sanitizeStudy(publicStudyService.get(studyId)
      .orElseThrow(entityNotFound(Study.class, studyId)));
  }

  @ApiOperation("Get one dataset by study ID and dataset ID")
  @GetJsonMapping("/studies/{studyId}/datasets/{datasetId}")
  public Dataset getDataset(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId) {
    return sanitizeDataset(publicStudyService.getDataset(studyId, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId)));
  }

  @ApiOperation("Get one instance variable by study ID, dataset ID and instance variable ID")
  @GetJsonMapping(path = "/studies/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  public InstanceVariable getInstanceVariable(
    @PathVariable UUID studyId,
    @PathVariable UUID datasetId,
    @PathVariable UUID instanceVariableId) {
    return sanitizeInstanceVariable(publicStudyService.getInstanceVariable(studyId, datasetId, instanceVariableId)
            .orElseThrow(entityNotFound(InstanceVariable.class, instanceVariableId)));
  }

  @ApiOperation("List all studies of given study group")
  @GetJsonMapping("/studyGroups/{studyGroupId}/studies")
  public List<Study> getStudyGroupStudies(
          @PathVariable("studyGroupId") UUID studyGroupId) {
    return sanitizeStudyList(publicStudyService.getStudiesByStudyGroup(studyGroupId));
  }

  @ApiOperation("Get next instance variable for given one")
  @GetJsonMapping("/studies/{studyId}/datasets/{datasetId}/instanceVariables/{instanceVariableId}/next")
  public String getNextInstanceVariable(
          @PathVariable("studyId") UUID studyId,
          @PathVariable("datasetId") UUID datasetId,
          @PathVariable("instanceVariableId") UUID instanceVariableId) {
    return publicStudyService.getNextInstanceVariable(studyId, datasetId, instanceVariableId);
  }
}
