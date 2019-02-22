package fi.thl.thldtkk.api.metadata.service.report.processingactivities.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Study;

public class ProcessingActivitiesModel {
  private Optional<Organization> organization;
  private List<Study> studies = Collections.emptyList();
  
  public List<Study> getStudies() {
    return studies;
  }
  public void setStudies(List<Study> studies) {
    this.studies = studies;
  }
  public Optional<Organization> getOrganization() {
    return organization;
  }
  public void setOrganization(Optional<Organization> organization) {
    this.organization = organization;
  }
}