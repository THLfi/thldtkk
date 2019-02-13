package fi.thl.thldtkk.api.metadata.service.report;

import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.report.StudyReportService;
import fi.thl.thldtkk.api.metadata.service.report.context.PrivacyNoticeContextFactory;
import fi.thl.thldtkk.api.metadata.service.report.context.ReportContextFactory;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class PrivacyNoticeReportService implements StudyReportService {

  private final EditorStudyService editorStudyService;
  private final TemplateEngine templateEngine;

  public PrivacyNoticeReportService(EditorStudyService editorStudyService, TemplateEngine templateEngine) {
    this.editorStudyService = editorStudyService;
    this.templateEngine = templateEngine;
  }

  @Override
  public byte[] generatePDFReport(UUID studyId, String lang) {
    ReportContextFactory privacyNoticeContextFactory = new PrivacyNoticeContextFactory(studyId, lang, editorStudyService);
    Context context = privacyNoticeContextFactory.makeContext();

    String template = templateEngine.process("privacy-notice", context);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    ITextRenderer renderer = new ITextRenderer();
    renderer.setDocumentFromString(template);
    renderer.layout();
    try {
      renderer.createPDF(bytes);
      bytes.close();
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to generate register description for study '" + studyId + "'", e);
    }

    return bytes.toByteArray();
  }

}
