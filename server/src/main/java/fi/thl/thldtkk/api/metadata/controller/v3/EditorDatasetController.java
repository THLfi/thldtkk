package fi.thl.thldtkk.api.metadata.controller.v3;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.v3.DatasetService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Editor API for datasets")
@RestController
@RequestMapping("/api/v3/editor/datasets")
public class EditorDatasetController {

  @Autowired
  @Qualifier("editorDatasetService")
  private DatasetService editorDatasetService;

  @ApiOperation("Search datasets")
  @GetJsonMapping
  public List<Dataset> queryDatasets(
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "datasetTypeId", required = false) UUID datasetTypeId,
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "max", defaultValue = "-1") int max) {
    return editorDatasetService.find(organizationId, datasetTypeId, query, max);
  }

  @ApiOperation("Get one dataset by ID")
  @GetJsonMapping("/{datasetId}")
  public Dataset getDataset(@PathVariable("datasetId") UUID datasetId) {
    return editorDatasetService.get(datasetId).orElseThrow(NotFoundException::new);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Dataset postDataset(
      @RequestParam(name = "saveInstanceVariables", defaultValue = "true") boolean saveInstanceVariables,
      @RequestBody @Valid Dataset dataset) {
    return saveInstanceVariables
        ? editorDatasetService.save(dataset)
        : editorDatasetService.saveNotUpdatingInstanceVariables(dataset);
  }

  @DeleteMapping("/{datasetId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteDataset(@PathVariable("datasetId") UUID datasetId) {
    editorDatasetService.delete(datasetId);
  }


}
