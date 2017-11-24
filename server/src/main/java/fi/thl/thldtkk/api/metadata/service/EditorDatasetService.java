package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import java.util.List;
import java.util.UUID;

public interface EditorDatasetService extends Service<UUID, Dataset> {

  List<Dataset> findAll(String language);
  
  List<Dataset> getDatasetsByUnitType(UUID unitTypeId);

  List<Dataset> getUniverseDatasets(UUID universeId);
  
}
