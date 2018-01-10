package fi.thl.thldtkk.api.metadata.service;

import java.util.UUID;

public interface StudyRegisterDescriptionService {

  byte[] generateRegisterDescriptionPdf(UUID studyId, String lang);

}
