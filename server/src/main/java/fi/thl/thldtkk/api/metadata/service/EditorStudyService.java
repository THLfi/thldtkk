package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Study;

import java.util.List;
import java.util.UUID;

public interface EditorStudyService extends Service<UUID, Study> {

  List<Study> find(UUID organizationId, String query, int max, String sortString);

}
