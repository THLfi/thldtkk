package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationService extends Service<UUID, Organization> {
  Optional<Organization> getByVirtuId(String virtuId);

  /**
   * Find all values with implementation specific flags that can be used to
   * restrict or expand the amount of requested data. For a more solid, but
   * less generic implementation, string flags could be replaced with enums in
   * the future.
   *
   * @return all values
   */
  List<Organization> findAllSelectively(List<String> queryFlags);
}
