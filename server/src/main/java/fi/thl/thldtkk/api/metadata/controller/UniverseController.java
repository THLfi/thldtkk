package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.UniverseService;
import fi.thl.thldtkk.api.metadata.domain.Universe;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "API for universes")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/universes")
public class UniverseController {

  @Autowired
  private UniverseService universeService;

  @Autowired
  private EditorDatasetService datasetService;

  @ApiOperation("List all universes")
  @GetJsonMapping
  public List<Universe> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return universeService.find(query, -1);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Universe save(@RequestBody @Valid Universe universe) {
    return universeService.save(universe);
  }

  @DeleteMapping("/{universeId}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("universeId") UUID universeId) {
    universeService.delete(universeId);
  }

  @ApiOperation("List all datasets of a universe")
  @GetJsonMapping("{universeId}/datasets")
  public List<Dataset> getUniverseDatasets(
          @PathVariable UUID universeId) {
    return datasetService.getUniverseDatasets(universeId);
  }
}
