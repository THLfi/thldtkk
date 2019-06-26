package fi.thl.thldtkk.api.metadata.service.report.processingactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import fi.thl.thldtkk.api.metadata.domain.AssociatedOrganization;
import fi.thl.thldtkk.api.metadata.domain.GroupOfRegistree;
import fi.thl.thldtkk.api.metadata.domain.LegalBasisForHandlingPersonalData;
import fi.thl.thldtkk.api.metadata.domain.Link;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.ReceivingGroup;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.report.processingactivities.model.ProcessingActivitiesModel;

public class ProcessingActivitiesReportView {
  
  private static final int STARTING_ROW = 9;
  private static final int STUDY_NAME_COL = 0;
  private static final int PURPOSE_OF_REGISTRY_COL = 1;
  private static final int LEGAL_BASIS_COL = 2;
  private static final int SHARED_REGISTRY_HOLDER_COL = 3;
  private static final int REGISTREE_GROUPS_COL = 4;
  private static final int PERSONAL_INFORMATION_GROUPS_COL = 5;
  private static final int RECEIVING_GROUPS_COL = 6;
  private static final int DATA_TRANSFERS_COL = 7;
  private static final int DATA_TRANSFERS_SECURITY_COL = 8;
  private static final int DATA_RETENTION_COL = 9;
  private static final int SECURITY_MEASURES_COL = 10;
  
  private final ProcessingActivitiesModel model;
  private final Sheet sheet;
  private final String lang;
  private final Locale locale;
  private final ResourceBundle messages;
  
  private int currentRow = STARTING_ROW;
  
  public ProcessingActivitiesReportView(ProcessingActivitiesModel model, Sheet sheet, String lang) {
    this.model = model;
    this.sheet = sheet;
    this.lang = lang;
    this.locale = new Locale(lang);
    this.messages = ResourceBundle.getBundle("i18n/processing-activities", locale);
  }
  
  public void render() {
    Optional<Organization> org = this.model.getOrganization();
    if (org.isPresent()) {
      renderOrganization(org.get());
    }
    this.model.getStudies().stream().forEach(study -> {
      renderStudy(study);
      this.currentRow++;
    });
  }
  
  public void renderOrganization(Organization org) {
    String organization = org.getPrefLabel().get(this.lang);
    String abbreviation = org.getAbbreviation().get(this.lang);
    if (abbreviation != null) {
      organization = String.format("%1$s (%2$s)", organization, abbreviation);
    }
    this.sheet
      .getRow(2)
      .getCell(1)
      .setCellValue(organization);
  }
  
  public void renderStudy(Study study) {
    Row row = this.sheet.createRow(currentRow);
    row.createCell(STUDY_NAME_COL).setCellValue(study.getPrefLabel().get(lang));
    row.createCell(PURPOSE_OF_REGISTRY_COL).setCellValue(study.getPurposeOfPersonRegistry().get(lang));

    String legalBasis = renderLegalBasis(study.getLegalBasisForHandlingPersonalData(), study.getOtherLegalBasisForHandlingPersonalData());
    row.createCell(LEGAL_BASIS_COL).setCellValue(legalBasis);

    String associatedOrganizations = renderAssociatedOrganizations(study.getAssociatedOrganizations());
    row.createCell(SHARED_REGISTRY_HOLDER_COL).setCellValue(associatedOrganizations);
    
    String otherGroupsOfRegistrees = study.getOtherGroupsOfRegistrees().get(lang);
    String groupsOfRegistrees = this.renderGroupsOfRegistrees(study.getGroupsOfRegistrees(), otherGroupsOfRegistrees);
    row.createCell(REGISTREE_GROUPS_COL).setCellValue(groupsOfRegistrees);
    
    row.createCell(PERSONAL_INFORMATION_GROUPS_COL).setCellValue(study.getUsageOfPersonalInformation().get(lang));
    
    String otherReceivingGroups = study.getOtherReceivingGroups().get(lang);
    String receivingGroups = this.renderReceivingGroups(study.getReceivingGroups(), otherReceivingGroups);
    row.createCell(RECEIVING_GROUPS_COL).setCellValue(receivingGroups);
    
    row.createCell(DATA_TRANSFERS_COL).setCellValue(study.getPersonRegisterDataTransfers().get(lang));
    row.createCell(DATA_TRANSFERS_SECURITY_COL).setCellValue(study.getPersonRegisterDataTransfersOutsideEuOrEta().get(lang));
    row.createCell(DATA_RETENTION_COL).setCellValue(study.getRetentionPeriod().get(lang));

    String securityMeasureLinks = renderLinks(study.getLinks());
    row.createCell(SECURITY_MEASURES_COL).setCellValue(securityMeasureLinks);
  }

  private String renderLinks(List<Link> links) {
    List<String> linkStrings = new ArrayList<>();
    for (Link link : links) {
      String linkName = link.getPrefLabel().get(lang);
      String linkUrl = link.getLinkUrl().get(lang);
      String linkString = String.format("%1$s (%2$s)", linkName, linkUrl);
      linkStrings.add(linkString);
    }
    return String.join(", ", linkStrings);
  }

private String renderLegalBasis(List<LegalBasisForHandlingPersonalData> legalBasisList, Map<String, String> otherLegalBasis) {
    List<String> legalBasisStrings = new ArrayList<>();
    for (LegalBasisForHandlingPersonalData legalBasis : legalBasisList) {
      if (legalBasis.equals(LegalBasisForHandlingPersonalData.OTHER)) {
        legalBasisStrings.add(otherLegalBasis.get(this.lang));
      } else {
        legalBasisStrings.add(messages.getString("legalBasisForHandlingPersonalData." + legalBasis.name()));
      }
    }
    return String.join(", ", legalBasisStrings);
  }

  private String renderAssociatedOrganizations(List<AssociatedOrganization> associatedOrganizations) {
      List<String> associatedOrganizationStrings = new ArrayList<>();
      for (AssociatedOrganization org : associatedOrganizations) {
        if (org.isRegistryOrganization().orElse(false)){
          associatedOrganizationStrings.add(org.getOrganization().get().getPrefLabel().get(this.lang));
        }
      }
      return String.join(", ", associatedOrganizationStrings);
  }

  public String renderGroupsOfRegistrees(List<GroupOfRegistree> groups, String otherGroups) {
      List<String> groupStrings = new ArrayList<>();
      for (GroupOfRegistree group : groups) {
        if (group.equals(GroupOfRegistree.OTHER) && otherGroups != null) {
          groupStrings.add(otherGroups);
        } else {
          groupStrings.add(messages.getString("groupsOfRegistrees." + group.name()));
        }
      }
    return String.join(", ", groupStrings);
  }
  
  public String renderReceivingGroups(List<ReceivingGroup> groups, String otherGroups) {
    List<String> groupStrings = new ArrayList<>();
    for (ReceivingGroup group : groups) {
      if (group.equals(ReceivingGroup.OTHER) && otherGroups != null) {
        groupStrings.add(otherGroups);
      } else {
        groupStrings.add(this.messages.getString("receivingGroups." + group.name()));
      }
    }
    return String.join(", ", groupStrings);
  }
}
