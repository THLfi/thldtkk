package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.Study;

public interface EmailService {
  void sendUnitInChargeConfirmationMessage(Study study, OrganizationUnit unit);
}
