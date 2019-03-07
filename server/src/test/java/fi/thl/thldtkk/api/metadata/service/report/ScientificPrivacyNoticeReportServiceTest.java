package fi.thl.thldtkk.api.metadata.service.report;

import fi.thl.thldtkk.api.metadata.ThymeleafConfiguration;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForDigitalSecurity;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForPhysicalSecurity;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.test.PersonInRoleBuilder;
import fi.thl.thldtkk.api.metadata.test.a;
import fi.thl.thldtkk.api.metadata.test.an;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ThymeleafConfiguration.class })
public class ScientificPrivacyNoticeReportServiceTest {

  ScientificPrivacyNoticeReportService service;

  @Mock
  EditorStudyService editorStudyService;
  @Autowired
  TemplateEngine templateEngine;

  @Before
  public void initService() {
    MockitoAnnotations.initMocks(this);
    service = new ScientificPrivacyNoticeReportService(editorStudyService, templateEngine);
  }

  @Test
  public void generatePdf() throws Exception {
    UUID studyId = nameUUIDFromString("study1");

    Organization organization = an.organization()
      .withPrefLabel("Terveyden ja hyvinvoinnin laitos")
      .withAbbreviation("THL")
      .withAddressForRegistryPolicy("Postiosoite: PL 30, 00271 Helsinki\nKäyntiosoite: Mannerheimintie 166, Helsinki")
      .withPhoneNumberForRegistryPolicy("+358 29 524 6000")
      .build();
    Person yhteyshenkiloPerson = a.person()
      .withFirstName("Yrjö")
      .withLastName("Yhteyshenkilö")
      .withPhone("+358 40 123 4567")
      .withEmail("yrjo.yhteyshenkilo@thl.fi")
      .build();
    PersonInRole yhteyshenkiloPir = new PersonInRoleBuilder()
      .withPerson(yhteyshenkiloPerson)
      .withRole("Yhteyshenkilö")
      .build();
    Person tutkimuksenVastuullinenJohtajaPerson = a.person()
      .withFirstName("Tiina")
      .withLastName("Tutkimuksenvastuullinenjohtaja")
      .withPhone("+358 50 234 5678")
      .withEmail("tiina.tutkimuksenvastuullinenjohtaja@thl.fi")
      .build();
    PersonInRole tutkimuksenVastuullinenJohtajaPir = new PersonInRoleBuilder()
      .withPerson(tutkimuksenVastuullinenJohtajaPerson)
      .withRole("Tutkimuksen vastuullinen johtaja")
      .build();
    Person tietosuojavastaavaPerson = a.person()
      .withFirstName("Taina")
      .withLastName("Tietosuojavastaava")
      .withPhone("+358 50 354 6789")
      .withEmail("taina.tietosuojavastaava@thl.fi")
      .build();
    PersonInRole tietosuojavastaavaPir = new PersonInRoleBuilder()
      .withPerson(tietosuojavastaavaPerson)
      .withRole("Tietosuojavastaava")
      .build();
    Study study = a.study()
      .withId(studyId)
      .withLastModifiedDate(Date.from(ZonedDateTime.parse("2018-01-01T12:00:00+02:00[Europe/Helsinki]").toInstant()))
      .withOwnerOrganization(organization)
      .withPersonInRoles(yhteyshenkiloPir, tutkimuksenVastuullinenJohtajaPir, tietosuojavastaavaPir)
      .withPrefLabel("Terveydenhuollon hoitoilmoitusrekisteri")
      .withDescription("Nämä tiedot tulevat...\n\n...kentästä 'description'.")
      .withPurposeOfPersonRegistry("Nämä tiedot tulevat...\n\n...kentästä 'purposeOfPersonRegistry'.")
      .withPartiesAndSharingOfResponsibilityInCollaborativeStudy("Nämä tiedot tulevat...\n\n...kentästä 'partiesAndSharingOfResponsibilityInCollaborativeStudy'.")
      .build();
    when(editorStudyService.get(eq(studyId))).thenReturn(Optional.of(study));

    byte[] content = service.generatePDFReport(studyId, "fi");

    assertThat(content).isNotEmpty();

    // Write PDF to a file for manual inspection
    String filePath = getClass().getResource(".").getFile() + File.separator + "scientific-privacy-notice.pdf";
    FileOutputStream fileOutput = new FileOutputStream(filePath);
    fileOutput.write(content);
    fileOutput.close();
  }

}
