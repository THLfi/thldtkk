package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.service.OrganizationUnitService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/organizationUnit")
public class OrganizationUnitController {

  @Autowired
  private OrganizationUnitService organizationUnitService;

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
}
