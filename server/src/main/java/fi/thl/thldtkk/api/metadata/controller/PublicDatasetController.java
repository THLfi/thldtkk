package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
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
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;

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
    return publicDatasetService.find(organizationId, datasetTypeId, query, max, sort);
  }

  @ApiOperation("Get one published dataset by ID")
  @GetJsonMapping("/{datasetId}")
  public Dataset getDataset(@PathVariable("datasetId") UUID datasetId) {
    return publicDatasetService.get(datasetId).orElseThrow(NotFoundException::new);
  }

}
