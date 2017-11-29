package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.StudyGroup;

import java.util.List;
import java.util.UUID;

public interface StudyGroupService extends Service<UUID, StudyGroup> {
  List<StudyGroup> findByOwnerOrganizationId(UUID organizationId, int max);
}
