package fi.thl.thldtkk.api.metadata.service;

import java.util.UUID;

public interface StudyPrivacyNoticeService {

  byte[] generatePrivacyNoticePdf(UUID studyId, String lang);

}
