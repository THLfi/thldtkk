package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicStudyService extends Service<UUID, Study> {
  
  List<Study> find(UUID organizationId, String query, int max, String sortString);

  Optional<Dataset> getDataset(UUID studyId, UUID datasetId);
  
  Optional<InstanceVariable> getInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId);

  List<Study> getStudiesByStudyGroup(UUID studyGroupId);
  
}
