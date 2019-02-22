package fi.thl.thldtkk.api.metadata.service.report.processingactivities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.thl.thldtkk.api.metadata.service.report.processingactivities.model.ProcessingActivitiesModel;

@Component
public class ProcessingActivitiesReportService {

  @Autowired
  ProcessingActivitiesModelFactory processingActivitiesModelFactory;

  public byte[] generateReport(UUID organizationId, String lang) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    URL path = classLoader.getResource("xlsx-templates/ProcessingActivitiesTemplate_fi.xlsx");
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    try (Workbook workbook = WorkbookFactory.create(new FileInputStream(path.getFile()))) {
      Sheet sheet = workbook.getSheetAt(0);

      ProcessingActivitiesModel processingActivitiesModel = processingActivitiesModelFactory.build(organizationId);
      ProcessingActivitiesReportView view = new ProcessingActivitiesReportView(processingActivitiesModel, sheet, lang);
      view.render();

      workbook.write(byteStream);
    } catch (IOException | EncryptedDocumentException e) {
      throw new RuntimeException("Failed to generate processing activities report for organization " + organizationId, e);
    }

    return byteStream.toByteArray();
  }
}
