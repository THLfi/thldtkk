package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.v3.DatasetService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Editor operations API for datasets")
@RestController
@RequestMapping("/api/v3/editor/dataset-functions")
public class EditorDatasetFunctionsController {

  @Autowired
  @Qualifier("editorDatasetService")
  private DatasetService editorDatasetService;

  @Autowired
  @Qualifier("publicDatasetService")
  private DatasetService publicDatasetService;

  @ApiOperation("Publish given dataset")
  @PostMapping("/publish")
  public void publishDataset(@RequestParam("datasetId") UUID datasetId) {
    Dataset dataset = editorDatasetService.get(datasetId).orElseThrow(NotFoundException::new);
    publicDatasetService.save(dataset);
  }

}
