package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.service.v3.DatasetPublishingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Editor operations API for datasets")
@RestController
@RequestMapping("/api/v3/editor/dataset-functions")
public class EditorDatasetFunctionsController {

  @Autowired
  private DatasetPublishingService datasetPublishingService;

  @ApiOperation("Publish given dataset")
  @GetMapping("/publish")
  public void publishDataset(@RequestParam("datasetId") UUID datasetId) {
    datasetPublishingService.publish(datasetId);
  }

  @ApiOperation("Withdraw (un-publish) given dataset")
  @GetMapping("/withdraw")
  public void withdrawDataset(@RequestParam("datasetId") UUID datasetId) {
    datasetPublishingService.withdraw(datasetId);
  }

}
