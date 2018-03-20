package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstanceQuestionService extends Service<UUID, InstanceQuestion> {

  List<InstanceQuestion> findDatasetInstanceQuestions(UUID studyId, UUID datasetId, String query);

  Optional<InstanceQuestion> getByPrefLabel(String prefLabel);
}
