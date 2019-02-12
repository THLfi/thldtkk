package fi.thl.thldtkk.api.metadata.service.impl;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.StudyPrivacyNoticeService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.apache.commons.lang.text.StrBuilder;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditorStudyPrivacyNoticeServiceImpl implements StudyPrivacyNoticeService {

  private static final String DATA_PROTECTION_PERSON = "Tietosuojavastaava";
  private static final String REGISTRY_SUPERVISOR = "Rekisterivastaava";

  private final EditorStudyService editorStudyService;
  private final TemplateEngine templateEngine;

  public EditorStudyPrivacyNoticeServiceImpl(EditorStudyService editorStudyService, TemplateEngine templateEngine) {
    this.editorStudyService = editorStudyService;
    this.templateEngine = templateEngine;
  }

  @Override
  public byte[] generatePrivacyNoticePdf(UUID studyId, String lang) {
    Context context = new Context(new Locale(lang));

    Study study = editorStudyService.get(studyId)
      .orElseThrow(NotFoundException.entityNotFound(Study.class, studyId));

    if (study.getOwnerOrganization().isPresent()) {
      Organization organization = study.getOwnerOrganization().get();
      context.setVariable("registrarName", getRegistrarName(organization, lang));
      context.setVariable("registrarAddress", organization.getAddressForRegistryPolicy().get("fi"));
      context.setVariable("registrarOtherInfo", organization.getPhoneNumberForRegistryPolicy().orElse(""));
    }

    if (!study.getPersonInRoles().isEmpty()) {
      PersonInRole pir = study.getPersonInRoles().iterator().next();
      Person contactPerson = pir.getPerson().get();
      context.setVariable("contactPersonName", getPersonName(contactPerson));
      context.setVariable("contactPersonOtherInfo", getPersonPhoneAndEmail(contactPerson));

      List<PersonInRole> personsWithAssociations = study.getPersonInRoles().stream()
        .filter(personInRole -> personInRole.getRole().isPresent())
        .filter(personInRole -> personInRole.getPerson().isPresent())
        .collect(Collectors.toList());

      Optional<PersonInRole> dataProtectionPerson = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get("fi").equals(DATA_PROTECTION_PERSON))
        .findFirst();

      Optional<PersonInRole> registrySupervisor = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get("fi").equals(REGISTRY_SUPERVISOR))
        .findFirst();

      Optional<PersonInRole> responsiblePerson =
        dataProtectionPerson.isPresent() ? dataProtectionPerson : registrySupervisor;

      if (responsiblePerson.isPresent()) {
        Person unwrappedPerson = responsiblePerson.get().getPerson().get();
        context.setVariable("dataProtectionPersonName", getPersonName(unwrappedPerson));
        context.setVariable("dataProtectionPersonContactInfo", getPersonPhoneAndEmail(unwrappedPerson));
      }
    }

    context.setVariable("studyName", study.getPrefLabel().get(lang));
    context.setVariable("purposeOfPersonRegister", study.getPurposeOfPersonRegistry().get(lang));
    context.setVariable("usageOfPersonalInformation", study.getUsageOfPersonalInformation().get(lang));
    context.setVariable("legalBasisValues", study.getLegalBasisForHandlingPersonalData());
    context.setVariable("otherLegalBasisValue", study.getOtherLegalBasisForHandlingPersonalData().get(lang));
    context.setVariable("containsSensitiveData", study.getContainsSensitivePersonalData().orElse(false));
    context.setVariable("legalBasisSensitiveValues", study.getLegalBasisForHandlingSensitivePersonalData());
    context.setVariable("otherLegalBasisSensitiveValue", study.getOtherLegalBasisForHandlingSensitivePersonalData().get(lang));

    context.setVariable("registerContent", study.getRegistryPolicy().get(lang));
    context.setVariable("registerSources", study.getPersonRegistrySources().get(lang));
    context.setVariable("dataTransfers", study.getPersonRegisterDataTransfers().get(lang));
    context.setVariable("dataTransfersOutsideEuOrEea", study.getPersonRegisterDataTransfersOutsideEuOrEta().get(lang));

    Optional<Boolean> profilingAndAutomation = study.getProfilingAndAutomation();
    String profilingAndAutomationDescription = "";
    if (profilingAndAutomation.orElse(false)) {
        profilingAndAutomationDescription = study.getProfilingAndAutomationDescription().get(lang);
    }
    context.setVariable("profilingAndAutomation", profilingAndAutomation);
    context.setVariable("profilingAndAutomationDescription", profilingAndAutomationDescription);

    context.setVariable("principlesForPhysicalSecurity", study.getPrinciplesForPhysicalSecurity());
    context.setVariable("principlesForDigitalSecurity", study.getPrinciplesForDigitalSecurity());

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

  @Override
  public byte[] generateScientificPrivacyNoticePdf(UUID studyId, String lang) {
    Context context = new Context(new Locale(lang));

    Study study = editorStudyService.get(studyId)
      .orElseThrow(NotFoundException.entityNotFound(Study.class, studyId));

    if (study.getOwnerOrganization().isPresent()) {
      Organization organization = study.getOwnerOrganization().get();
      context.setVariable("registrarName", getRegistrarName(organization, lang));
      context.setVariable("registrarAddress", organization.getAddressForRegistryPolicy().get("fi"));
      context.setVariable("registrarOtherInfo", organization.getPhoneNumberForRegistryPolicy().orElse(""));
    }

    if (!study.getPersonInRoles().isEmpty()) {
      PersonInRole pir = study.getPersonInRoles().iterator().next();
      Person contactPerson = pir.getPerson().get();
      context.setVariable("contactPersonName", getPersonName(contactPerson));
      context.setVariable("contactPersonOtherInfo", getPersonPhoneAndEmail(contactPerson));

      List<PersonInRole> personsWithAssociations = study.getPersonInRoles().stream()
        .filter(personInRole -> personInRole.getRole().isPresent())
        .filter(personInRole -> personInRole.getPerson().isPresent())
        .collect(Collectors.toList());

      Optional<PersonInRole> dataProtectionPerson = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get("fi").equals(DATA_PROTECTION_PERSON))
        .findFirst();

      Optional<PersonInRole> registrySupervisor = personsWithAssociations.stream()
        .filter(person -> person.getRole().get().getPrefLabel().get("fi").equals(REGISTRY_SUPERVISOR))
        .findFirst();

      Optional<PersonInRole> responsiblePerson =
        dataProtectionPerson.isPresent() ? dataProtectionPerson : registrySupervisor;

      if (responsiblePerson.isPresent()) {
        Person unwrappedPerson = responsiblePerson.get().getPerson().get();
        context.setVariable("dataProtectionPersonName", getPersonName(unwrappedPerson));
        context.setVariable("dataProtectionPersonContactInfo", getPersonPhoneAndEmail(unwrappedPerson));
      }
    }

    context.setVariable("description", study.getDescription().get(lang));
    context.setVariable("studyName", study.getPrefLabel().get(lang));
    context.setVariable("purposeOfPersonRegister", study.getPurposeOfPersonRegistry().get(lang));
    context.setVariable("usageOfPersonalInformation", study.getUsageOfPersonalInformation().get(lang));
    context.setVariable("legalBasisValues", study.getLegalBasisForHandlingPersonalData());
    context.setVariable("otherLegalBasisValue", study.getOtherLegalBasisForHandlingPersonalData().get(lang));
    context.setVariable("containsSensitiveData", study.getContainsSensitivePersonalData().orElse(false));
    context.setVariable("legalBasisSensitiveValues", study.getLegalBasisForHandlingSensitivePersonalData());
    context.setVariable("otherLegalBasisSensitiveValue", study.getOtherLegalBasisForHandlingSensitivePersonalData().get(lang));

    context.setVariable("registerContent", study.getRegistryPolicy().get(lang));
    context.setVariable("registerSources", study.getPersonRegistrySources().get(lang));
    context.setVariable("dataTransfers", study.getPersonRegisterDataTransfers().get(lang));
    context.setVariable("dataTransfersOutsideEuOrEea", study.getPersonRegisterDataTransfersOutsideEuOrEta().get(lang));

    Optional<Boolean> profilingAndAutomation = study.getProfilingAndAutomation();
    String profilingAndAutomationDescription = "";
    if (profilingAndAutomation.orElse(false)) {
      profilingAndAutomationDescription = study.getProfilingAndAutomationDescription().get(lang);
    }
    context.setVariable("profilingAndAutomation", profilingAndAutomation);
    context.setVariable("profilingAndAutomationDescription", profilingAndAutomationDescription);

    context.setVariable("principlesForPhysicalSecurity", study.getPrinciplesForPhysicalSecurity());
    context.setVariable("principlesForDigitalSecurity", study.getPrinciplesForDigitalSecurity());

    String template = templateEngine.process("scientific-privacy-notice", context);

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

  private String getRegistrarName(Organization organization, String lang) {
    StringBuilder name = new StringBuilder();
    name.append(organization.getPrefLabel().get(lang));

    String abbreviation = organization.getAbbreviation().get(lang);
    if (StringUtils.hasText(abbreviation)) {
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

  private String getPersonPhoneAndEmail(Person person) {
    StrBuilder info = new StrBuilder();
    person.getPhone().ifPresent(phone -> info.append(phone));
    person.getEmail().ifPresent(email -> {
      info.appendSeparator(", ");
      info.append(email);
    });
    return info.toString();
  }

}



