package fi.thl.thldtkk.api.metadata.service.v3;

import java.util.UUID;

public interface DatasetPublishingService {

  void publish(UUID datasetId);

  void withdraw(UUID datasetId);

}
