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
import java.util.Locale;
import java.util.UUID;

public class EditorStudyPrivacyNoticeServiceImpl implements StudyPrivacyNoticeService {

  private static final String DATA_PROTECTION_PERSON = "Tietosuojavastaava";

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

    context.setVariable("lastModifiedDate", study.getLastModifiedDate().orElse(new Date()));

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

      for (PersonInRole personInRole : study.getPersonInRoles()) {
        if (personInRole.getRole().isPresent() && personInRole.getPerson().isPresent()
          && personInRole.getRole().get().getPrefLabel().get("fi").equals(DATA_PROTECTION_PERSON)) {

          Person dataProtectionPerson = personInRole.getPerson().get();
          context.setVariable("dataProtectionPersonName", getPersonName(dataProtectionPerson));
          context.setVariable("dataProtectionPersonContactInfo", getPersonPhoneAndEmail(dataProtectionPerson));
          break;
        }
      }
    }

    context.setVariable("registerName", study.getPrefLabel().get(lang));
    context.setVariable("purposeOfPersonRegister", study.getPurposeOfPersonRegistry().get(lang));
    context.setVariable("registerContent", study.getRegistryPolicy().get(lang));
    context.setVariable("registerSources", study.getPersonRegistrySources().get(lang));
    context.setVariable("dataTransfers", study.getPersonRegisterDataTransfers().get(lang));
    context.setVariable("dataTransfersOutsideEuOrEea", study.getPersonRegisterDataTransfersOutsideEuOrEta().get(lang));
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



