package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;

import static fi.thl.thldtkk.api.metadata.util.PublicFieldIgnoreUtil.sanitizeDataset;
import static fi.thl.thldtkk.api.metadata.util.PublicFieldIgnoreUtil.sanitizeDatasetList;

@Api(description = "Public API for datasets")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/public/datasets")
public class PublicDatasetController {

  @Autowired
  @Qualifier("publicDatasetService")
  private PublicDatasetService publicDatasetService;

  @ApiOperation("List all published datasets")
  @GetJsonMapping
  public List<Dataset> queryDatasets(
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "datasetTypeId", required = false) UUID datasetTypeId,
      @RequestParam(name = "max", defaultValue = "-1") int max,
      @RequestParam(name = "sort", defaultValue = "") String sort) {
    return sanitizeDatasetList(publicDatasetService.find(organizationId, datasetTypeId, query, max, sort));
  }

  @ApiOperation("Get one published dataset by ID")
  @GetJsonMapping("/{datasetId}")
  public Dataset getDataset(
    @PathVariable("datasetId") UUID datasetId,
    @RequestParam(name = "select", required = false) String selectString
  ) throws IOException {
    Optional<Dataset> dataset;
    if (selectString == null) {
      dataset = publicDatasetService.get(datasetId);
    } else {
      dataset = publicDatasetService.get(datasetId, ControllerUtils.parseSelect(selectString));
    }

    return sanitizeDataset(dataset.orElseThrow(NotFoundException::new));
  }

}
