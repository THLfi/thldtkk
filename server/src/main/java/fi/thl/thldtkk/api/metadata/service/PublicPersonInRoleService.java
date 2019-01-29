package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.PersonInRole;

import java.util.List;
import java.util.UUID;

public interface PublicPersonInRoleService extends Service<UUID, PersonInRole> {

  List<PersonInRole> getPersonInRoles(UUID personId);

  void deletePersonInRoles(UUID personId);
}
