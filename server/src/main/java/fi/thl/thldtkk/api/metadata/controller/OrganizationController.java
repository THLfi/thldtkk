package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "API for organizations")
@RestController
@RequestMapping("/api/v3/organizations")
public class OrganizationController {

  @Autowired
  private OrganizationService organizationService;

  @ApiOperation("List all organizations")
  @GetJsonMapping
  public List<Organization> queryOrganizations() {
    return organizationService.findAll();
  }
  
  @ApiOperation("Get a single organization based on its id")
  @GetJsonMapping("/{organization}")
  public Organization getOrganization(@PathVariable("organization") UUID organizationId) {
    return organizationService.get(organizationId).orElseThrow(NotFoundException::new);
  }

}
