package fi.thl.thldtkk.api.metadata.service.report.context;

import org.thymeleaf.context.Context;

/**
 * ReportContextFactory is used to build a basic context for report templates with the
 * goal of sharing context creation logic between similar reports.
 */
public interface ReportContextFactory {

    public Context makeContext();
}