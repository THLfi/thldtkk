package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.OrganizationUnitService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/organizationUnits")
public class OrganizationUnitController {

  @Autowired
  private OrganizationUnitService organizationUnitService;
  @Autowired
  private EditorStudyService editorStudyService;
  @Autowired
  private EditorDatasetService editorDatasetService;

  @GetJsonMapping
  public List<OrganizationUnit> queryOrganizationUnits() {
    return organizationUnitService.findAll();
  }

  @GetJsonMapping("/{organizationUnitId}")
  public OrganizationUnit getOrganizationUnit(@PathVariable("organizationUnitId") UUID id) {
    return organizationUnitService.get(id).orElseThrow(NotFoundException::new);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public OrganizationUnit save(@RequestBody @Valid OrganizationUnit organizationUnit) {
    return organizationUnitService.save(organizationUnit.getParentOrganizationId(), organizationUnit);
  }

  @DeleteMapping("/{organizationUnitId}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("organizationUnitId") UUID id) {
    organizationUnitService.delete(id);
  }

  @GetJsonMapping("/{organizationUnitId}/studies")
  public List<Study> getAssociatedStudies(@PathVariable UUID organizationUnitId) {
    return editorStudyService.getOrganizationUnitStudies(organizationUnitId);
  }

  @GetJsonMapping("/{organizationUnitId}/datasets")
  public List<Dataset> getAssociatedDatasets(@PathVariable UUID organizationUnitId) {
    return editorDatasetService.getOrganizationUnitDatasets(organizationUnitId);
  }

}
