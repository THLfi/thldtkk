package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.ThymeleafConfiguration;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForDigitalSecurity;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForPhysicalSecurity;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.report.StudyReportService;
import fi.thl.thldtkk.api.metadata.service.report.RegisterDescriptionReportService;
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
public class RegisterDescriptionReportServiceTest {

  StudyReportService registerDescriptionReportService;

  @Mock
  EditorStudyService editorStudyService;
  @Autowired
  TemplateEngine templateEngine;

  @Before
  public void initService() {
    MockitoAnnotations.initMocks(this);
    registerDescriptionReportService = new RegisterDescriptionReportService(editorStudyService, templateEngine);
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
    Person person = a.person()
      .withFirstName("Jutta")
      .withLastName("Järvelin")
      .withPhone("+358 50 123 4567")
      .withEmail("jutta.jarvelin@thl.fi")
      .build();
    PersonInRole pir = new PersonInRoleBuilder()
      .withPerson(person)
      .build();
    Study study = a.study()
      .withId(studyId)
      .withLastModifiedDate(Date.from(ZonedDateTime.parse("2018-01-01T12:00:00+02:00[Europe/Helsinki]").toInstant()))
      .withOwnerOrganization(organization)
      .withPersonInRoles(pir)
      .withPrefLabel("Terveydenhuollon hoitoilmoitusrekisteri")
      .withPurposeOfPersonRegistry(
        "Lainsäännökset:\n\n" +
        "Laki terveydenhuollon valtakunnallisista henkilörekistereistä (556/89) 3 §\n\n" +
        "Asetus terveydenhuollon valtakunnallisista henkilörekistereistä (774/89) 1 ja 2 §, siten kuin ne ovat 30.12.1993/1671 muutetussa asetuksessa.\n\n" +
        "Laki Terveyden ja hyvinvoinnin laitoksesta (668/2008) 2 §.\n\n" +
        "Erikoissairaanhoitolaki (1062/89) 5 §, kansanterveyslaki (66/72) 4. §, mielenterveyslaki (1116/90) 2 §.")
      .withRegistryPolicy(
        "Terveydenhuollon hoitoilmoitusrekisteri on jatkoa poistoilmoitusrekisterille, johon on kerätty tiedot vuosina 1969-1993 sairaaloista poistetuista potilaista. Rekisterin tietosisältöön on tänä aikana tehty erilaisia muutoksia. Oleellisin tietosisältö on pysynyt samana tietojen kuvatessa sairaalapalvelujen tuottajaa, potilasta, hoitoon tuloa ja hoidosta poistumista sekä potilaan diagnooseja ja saamaa hoitoa.\n\n" +
        "Poistoilmoitusrekisteri korvattiin v. 1994 alusta lukien hoitoilmoitusrekisterillä. Hoitoilmoitusrekisteri sisältää tietoja palvelujen käytöstä ja käyttäjistä laajemmin kuin poistoilmoitusrekisteri. Kun viimeksi mainittu sisälsi tietoja vain sairaaloiden vuodeosastolta poistetuista potilaista, sisältää terveydenhuollon hoitoilmoitusrekisteri tietoja...")
      .withPersonRegistrySources(
        "Ohjeet hoitoilmoitustietojen lähettämisestä annetaan vuosittain tai joka toinen vuosi ohjekirjassa (Hilmo; Sosiaalihuollon ja terveydenhuollon hoitoilmoitus, Määrittelyt ja ohjeistus). Siinä on annettu ohjeet siitä,\n\n" +
        "- mitkä terveyspalvelujen tuottajat (sairaalat, terveyskeskukset ja kotisairaanhoitopalvelujen tuottajat) ovat velvollisia lähettämään tietoja rekisteriin\n" +
        "- tietojen luovutuksen perusteet,\n" +
        "- mikä on tietosisältö,\n" +
        "- miten tiedot toimitetaan rekisteriin ja\n" +
        "- miten huolehditaan tietojen salassapidosta.\n\n" +
        "Tiedot lähetetään rekisterin ylläpitäjälle sähköisesti suojattua yhteyttä käyttäen tai kirjatuissa kirjeissä CD-levyilla tai muistitikuilla kryptattuna. Kunkin vuoden hoitoilmoitusrekisteriaineisto kootaan, tarkistetaan ja korjataan valtakunnalliseksi rekisteriksi seuraavan vuoden alussa.")
      .withPersonRegisterDataTransfers(
        "Rekisteritietoja luovutettaessa noudatetaan terveydenhuollon valtakunnallisista henkilörekistereistä annetun lain (556/92) 4 §:n määräyksiä, ts. salassa pidettäviä tietoja annetaan henkilötunnisteellisena vain luvan perusteella määrättyä tieteellistä tutkimusta varten.")
      .withPersonRegisterDataTransfersOutsideEuOrEea("Ei siirtoja.")
      .withPrinciplesForPhysicalSecurity(PrincipleForPhysicalSecurity.LOCKED_SPACE)
      .withPrinciplesForDigitalSecurity(PrincipleForDigitalSecurity.USERNAME, PrincipleForDigitalSecurity.PASSWORD, PrincipleForDigitalSecurity.LOGGING)
      .build();
    when(editorStudyService.get(eq(studyId))).thenReturn(Optional.of(study));

    byte[] content = registerDescriptionReportService.generatePDFReport(studyId, "fi");

    assertThat(content).isNotEmpty();

    // Write PDF to a file for manual inspection
    String filePath = getClass().getResource(".").getFile() + File.separator + "register-description.pdf";
    FileOutputStream fileOutput = new FileOutputStream(filePath);
    fileOutput.write(content);
    fileOutput.close();
  }

}
