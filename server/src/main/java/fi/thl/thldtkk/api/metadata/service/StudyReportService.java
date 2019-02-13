package fi.thl.thldtkk.api.metadata.service;

import java.util.UUID;

public interface StudyReportService {

  byte[] generatePDFReport(UUID studyId, String lang);
}
