package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/organizations")
public class OrganizationController2 {

    @Autowired
    private Service<UUID, Organization> organizationService;

    @GetJsonMapping
    public List<Organization> queryOrganizations() {
        return organizationService.query().collect(toList());
    }

    @GetJsonMapping("/{organization}")
    public Organization getDataset(@PathVariable("organization") UUID organizationId) {
        return organizationService.get(organizationId).orElseThrow(NotFoundException::new);
    }

}
