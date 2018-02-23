package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Study;

import java.util.UUID;

public interface StudyPublishingService {

  Study publish(UUID studyId);

  Study withdraw(UUID studyId);

  Study reissue(UUID studyId);
}
