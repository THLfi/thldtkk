package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
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
@RequestMapping("/api/v2/organizationUnits")
public class OrganizationUnitController2 {

    @Autowired
    private Service<UUID, OrganizationUnit> organizationUnitService;

    @GetJsonMapping
    public List<OrganizationUnit> queryOrganizationUnits() {
        return organizationUnitService.query().collect(toList());
    }

    @GetJsonMapping("/{organizationUnit}")
    public OrganizationUnit getDataset(@PathVariable("organizationUnit") UUID organizationUnitId) {
        return organizationUnitService.get(organizationUnitId).orElseThrow(NotFoundException::new);
    }

}
