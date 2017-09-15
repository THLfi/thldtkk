package fi.thl.thldtkk.api.metadata.service.v3;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import java.util.List;
import java.util.UUID;

public interface PublicDatasetService extends Service<UUID, Dataset> {

  List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max);
  
  List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max, String sort);

}