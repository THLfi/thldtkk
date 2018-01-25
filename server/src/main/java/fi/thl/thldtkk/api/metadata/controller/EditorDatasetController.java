package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Editor API for datasets")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/editor/datasets")
public class EditorDatasetController {

  @Autowired
  private EditorDatasetService editorDatasetService;

  @ApiOperation("Get all datasets available to the user with results sorted by the given language")
  @GetJsonMapping
  public List<Dataset> getDatasets(
    @RequestParam(value = "lang", defaultValue = "fi") String language) {
    return editorDatasetService.findAll(language);
  }

}
