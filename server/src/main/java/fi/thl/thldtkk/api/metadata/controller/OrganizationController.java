package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/organizations")
public class OrganizationController {

  @Autowired
  private OrganizationService organizationService;

  @GetJsonMapping
  public List<Organization> queryOrganizations() {
    return organizationService.findAll();
  }

  @GetJsonMapping("/{organization}")
  public Organization getOrganization(@PathVariable("organization") UUID organizationId) {
    return organizationService.get(organizationId).orElseThrow(NotFoundException::new);
  }

}
