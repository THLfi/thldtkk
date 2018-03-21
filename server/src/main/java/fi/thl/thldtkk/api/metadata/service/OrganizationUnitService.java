package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationUnitService extends Service<UUID, OrganizationUnit> {

    Optional<OrganizationUnit> findByAbbreviation(String abbreviation);

    OrganizationUnit save(UUID parentOrganizationId, OrganizationUnit organizationUnit);
    OrganizationUnit save(OrganizationUnit organizationUnit);
}
