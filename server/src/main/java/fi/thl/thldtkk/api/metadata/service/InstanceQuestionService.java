package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import java.util.List;
import java.util.UUID;

public interface InstanceQuestionService extends Service<UUID, InstanceQuestion> {

  List<InstanceQuestion> findDatasetInstanceQuestions(UUID datasetId, String query);

}
