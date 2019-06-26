package fi.thl.thldtkk.api.metadata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.report.processingactivities.ProcessingActivitiesReportService;

import java.time.LocalDate;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(API.PATH_WITH_VERSION + "/editor/organizations")
public class ProcessingActivitiesReportController {

  private static final String APPLICATION_XSLX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String FILE_EXTENSION = ".xlsx";

  @Autowired
  private ProcessingActivitiesReportService processingActivitiesReportService;

  @AdminOnly
  @RequestMapping(value = "/{organizationId}/processing-activities", produces = APPLICATION_XSLX)
  public @ResponseBody byte[] generatePdf(
    @PathVariable UUID organizationId,
    @RequestParam(value = "lang", defaultValue = "fi") String lang,
    HttpServletResponse response
  ) throws Exception {
    String fileName = String.format("processing-activities_%tF", LocalDate.now());
    response.setHeader("Content-Disposition", "attachment; filename=" + fileName + FILE_EXTENSION);
    return processingActivitiesReportService.generateReport(organizationId, lang);
  }

}
