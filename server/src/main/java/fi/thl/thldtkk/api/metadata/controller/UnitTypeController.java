package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.EditorInstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.UnitTypeService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/unitTypes")
public class UnitTypeController {

  @Autowired
  private UnitTypeService unitTypeService;

  @Autowired
  private EditorDatasetService datasetService;

  @Autowired
  private EditorInstanceVariableService instanceVariableService;

  @GetJsonMapping
  public List<UnitType> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return unitTypeService.find(query, -1);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public UnitType save(@RequestBody @Valid UnitType unitType) {
    return unitTypeService.save(unitType);
  }

  @DeleteMapping("/{unitTypeId}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("unitTypeId") UUID unitTypeId){
    unitTypeService.delete(unitTypeId);
  }

  @GetJsonMapping("/{unitTypeId}/datasets")
  public List<Dataset> getAssociatedDatasets(
    @PathVariable("unitTypeId") UUID unitTypeId) {
    return datasetService.getDatasetsByUnitType(unitTypeId);
  }

  @GetJsonMapping("/{unitTypeId}/instanceVariables")
  public List<InstanceVariable> getAssociatedInstanceVariables(
    @PathVariable("unitTypeId") UUID unitTypeId) {
    return instanceVariableService.getInstanceVariablesByUnitType(unitTypeId);
  }

}
