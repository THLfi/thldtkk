package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.DatasetType;
import fi.thl.thldtkk.api.metadata.service.DatasetTypeService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "API for dataset types")
@RestController
@RequestMapping("/api/v3/datasetTypes")
public class DatasetTypeController {

  @Autowired
  private DatasetTypeService datasetTypeService;

  @ApiOperation("Lists all dataset types that categorize datasets based on "
          + "e.g. whether the dataset originates from observations or registers")
  @GetJsonMapping
  public List<DatasetType> query() {
    return datasetTypeService.findAll();
  }

  @ApiOperation("Finds a single dataset type based on its id")
  @GetJsonMapping("/{datasetTypeId}")
  public DatasetType findById(@PathVariable("datasetTypeId") UUID datasetTypeId) {
    return datasetTypeService.get(datasetTypeId).orElseThrow(NotFoundException::new);
  }

}
