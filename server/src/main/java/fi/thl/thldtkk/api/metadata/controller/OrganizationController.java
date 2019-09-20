package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api(description = "API for organizations")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/organizations")
public class OrganizationController {

  @Autowired
  private OrganizationService organizationService;

  @ApiOperation("List all organizations")
  @GetJsonMapping
  public List<Organization> queryOrganizations(@RequestParam(defaultValue="true") boolean includeReferences) {
    List<String> queryFlags = new ArrayList<>();
    if (includeReferences) {
      queryFlags.add("includeReferences");
    }
    return organizationService.findAllSelectively(queryFlags);
  }

  @ApiOperation("Get a single organization based on its id")
  @GetJsonMapping("/{organization}")
  public Organization getOrganization(@PathVariable("organization") UUID organizationId) {
    return organizationService.get(organizationId).orElseThrow(NotFoundException::new);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Organization save(@RequestBody @Valid Organization organization) {
    return organizationService.save(organization);
  }

}
