package fi.thl.thldtkk.api.metadata.service.report.context;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.apache.commons.lang.text.StrBuilder;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToNull;

public class PrivacyNoticeContextFactory implements ReportContextFactory {

  private static final String DATA_PROTECTION_PERSON = "Tietosuojavastaava";
  private static final String REGISTRY_SUPERVISOR = "Rekisterivastaava";
  private static final String CONTACT_PERSON = "Yhteyshenkilö";
  private static final String PRINCIPAL_INVESTIGATOR = "Tutkimuksen vastuullinen johtaja";

  private final UUID studyId;
  private final String lang;
  private final EditorStudyService editorStudyService;

  public PrivacyNoticeContextFactory(UUID studyId, String lang, EditorStudyService editorStudyService) {
    this.studyId = studyId;
    this.lang = lang;
    this.editorStudyService = editorStudyService;
  }

  @Override
  public Context makeContext() {
    Context context = new Context(new Locale(lang));

    Study study = editorStudyService.get(studyId)
      .orElseThrow(NotFoundException.entityNotFound(Study.class, studyId));


    context.setVariable("editDate", study.getLastModifiedDate().orElse(new Date()));

    if (study.getOwnerOrganization().isPresent()) {
      Organization organization = study.getOwnerOrganization().get();
      context.setVariable("controllerName", getRegistrarName(organization, lang));
      context.setVariable("controllerAddress", organization.getAddressForRegistryPolicy().get(lang));
      context.setVariable("controllerOtherInfo", organization.getPhoneNumberForRegistryPolicy().orElse(""));
    }

    if (!study.getPersonInRoles().isEmpty()) {

      List<PersonInRole> personsWithAssociations = study.getPersonInRoles().stream()
        .filter(personInRole -> personInRole.getRole().isPresent())
        .filter(personInRole -> personInRole.getPerson().isPresent())
        .collect(Collectors.toList());

      Optional<PersonInRole> contactPerson = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get(lang).equals(CONTACT_PERSON))
        .findFirst();

      Optional<PersonInRole> registrySupervisor = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get(lang).equals(REGISTRY_SUPERVISOR))
        .findFirst();

      Optional<PersonInRole> shownContactPerson =
        contactPerson.isPresent() ? contactPerson : registrySupervisor;

      if (shownContactPerson.isPresent()) {
        Person unwrappedPerson = shownContactPerson.get().getPerson().get();
        context.setVariable("contactPerson", unwrappedPerson);
        context.setVariable("contactPersonName", getPersonName(unwrappedPerson));
        context.setVariable("contactPersonNameAndContactInformation", getPersonNameAndContactInformation(unwrappedPerson));
      }

      Optional<PersonInRole> dataProtectionPerson = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get(lang).equals(DATA_PROTECTION_PERSON))
        .findFirst();

      if (dataProtectionPerson.isPresent()) {
        Person unwrappedPerson = dataProtectionPerson.get().getPerson().get();
        context.setVariable("dataProtectionPerson", unwrappedPerson);
        context.setVariable("dataProtectionPersonName", getPersonName(unwrappedPerson));
      }

      personsWithAssociations.stream()
        .filter(personInRole -> personInRole.getRole().get().getPrefLabel().get(lang).equals(PRINCIPAL_INVESTIGATOR))
        .findFirst()
        .flatMap(PersonInRole::getPerson)
        .ifPresent(principalInvestigator -> {
          context.setVariable("principalInvestigatorName", getPersonName(principalInvestigator));
          context.setVariable("principalInvestigatorPhone", principalInvestigator.getPhone().orElse(""));
          context.setVariable("principalInvestigatorEmail", principalInvestigator.getEmail().orElse(""));
        });
    }

    context.setVariable("description", getLangValue(study.getDescription()));
    context.setVariable("studyName", getLangValue(study.getPrefLabel()));
    context.setVariable("studyType", study.getStudyType());
    context.setVariable("purposeOfPersonRegister", getLangValue(study.getPurposeOfPersonRegistry()));
    context.setVariable("usageOfPersonalInformation", getLangValue(study.getUsageOfPersonalInformation()));
    context.setVariable("legalBasisValues", study.getLegalBasisForHandlingPersonalData());
    context.setVariable("otherLegalBasisValue", getLangValue(study.getOtherLegalBasisForHandlingPersonalData()));
    context.setVariable("containsSensitiveData", study.getContainsSensitivePersonalData().orElse(false));
    context.setVariable("legalBasisSensitiveValues", study.getLegalBasisForHandlingSensitivePersonalData());
    context.setVariable("otherLegalBasisSensitiveValue", getLangValue(study.getOtherLegalBasisForHandlingSensitivePersonalData()));
    context.setVariable("typeOfSensitivePersonalDataValues", study.getTypeOfSensitivePersonalData());
    context.setVariable("otherTypeOfSensitivePersonalDataValue", getLangValue(study.getOtherTypeOfSensitivePersonalData()));

    context.setVariable("registerSources", getLangValue(study.getPersonRegistrySources()));
    context.setVariable("dataTransfers", getLangValue(study.getPersonRegisterDataTransfers()));
    context.setVariable("dataTransfersOutsideEuOrEea", getLangValue(study.getPersonRegisterDataTransfersOutsideEuOrEta()));

    context.setVariable("studyPerformers", study.getStudyPerformers().get(lang));

    Optional<Boolean> profilingAndAutomation = study.getProfilingAndAutomation();
    String profilingAndAutomationDescription = "";
    if (profilingAndAutomation.orElse(false)) {
        profilingAndAutomationDescription = getLangValue(study.getProfilingAndAutomationDescription());
    }
    context.setVariable("profilingAndAutomation", profilingAndAutomation);
    context.setVariable("profilingAndAutomationDescription", profilingAndAutomationDescription);

    context.setVariable("principlesForPhysicalSecurity", study.getPrinciplesForPhysicalSecurity());
    context.setVariable("principlesForDigitalSecurity", study.getPrinciplesForDigitalSecurity());

    context.setVariable("dataProcessingStartDate", study.getDataProcessingStartDate());
    context.setVariable("dataProcessingEndDate", study.getDataProcessingEndDate());

    context.setVariable("partiesAndSharingOfResponsibilityInCollaborativeStudy", getLangValue(study.getPartiesAndSharingOfResponsibilityInCollaborativeStudy()));
    context.setVariable("organizationsAreJointControllers", study.getOrganizationsAreJointControllers().orElse(false));

    return context;
  }

  private String getLangValue(Map<String, String> langValueMap) {
    return langValueMap != null ? trimToNull(langValueMap.get(lang)) : null;
  }

  private String getRegistrarName(Organization organization, String lang) {
    StringBuilder name = new StringBuilder();
    name.append(organization.getPrefLabel().get(lang));

    String abbreviation = organization.getAbbreviation().get(lang);
    if (isNotBlank(abbreviation)) {
      name.append(" (");
      name.append(abbreviation);
      name.append(")");
    }

    return name.toString();
  }

  private String getPersonName(Person person) {
    StringBuilder name = new StringBuilder();
    name.append(person.getFirstName().get());
    if (person.getLastName().isPresent()) {
      name.append(' ');
      name.append(person.getLastName().get());
    }
    return name.toString();
  }

  private String getPersonNameAndContactInformation(Person person) {
    StrBuilder output = new StrBuilder(getPersonName(person));
    if (person.getPhone().isPresent()) {
      output.appendNewLine();
      output.append(person.getPhone().get());
    }
    if (person.getEmail().isPresent()) {
      output.appendNewLine();
      output.append(person.getEmail().get());
    }
    return output.toString();
  }

}
