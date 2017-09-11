package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.service.v3.OrganizationUnitService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/organizationUnits")
public class OrganizationUnitController3 {

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

}
