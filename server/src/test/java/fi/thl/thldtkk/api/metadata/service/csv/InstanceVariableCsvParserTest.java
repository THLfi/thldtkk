package fi.thl.thldtkk.api.metadata.service.csv;

import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.Service;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InstanceVariableCsvParserTest {

  @Autowired
  private Service<UUID, CodeList> codeListService;
    
  @Test
  public void noEncodingProvided() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-all-fields.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, null, codeListService).parse();
    assertThat(results.getMessages()).containsExactly("import.csv.error.noEncoding");

    results = new InstanceVariableCsvParser(csv, "    ", codeListService).parse();
    assertThat(results.getMessages()).containsExactly("import.csv.error.noEncoding");
  }

  @Test
  public void unsupportedEncoding() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-all-fields.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "this is not valid encoding", codeListService).parse();

    assertThat(results.getMessages()).containsExactly("import.csv.error.unsupportedEncoding");
  }

  @Test
  public void csvWithAllFields() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-all-fields.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "MacRoman", codeListService).parse();

    assertThat(results.getParsedObject().get()).hasSize(1);
    InstanceVariable iv = results.getParsedObject().get().iterator().next().getParsedObject().get();
    assertThat(iv.getPrefLabel().get("fi")).isEqualTo("Sukupuoli");
    assertThat(iv.getTechnicalName().orElse(null)).isEqualTo("sukup_2017");
    assertThat(iv.getDescription().get("fi")).isEqualTo("Henkilön sukupuoli, kolme vaihtoehtoa.");
    assertThat(iv.getPartOfGroup().get("fi")).isEqualTo("Taustatiedot");
    assertThat(iv.getFreeConcepts().get("fi")).isEqualTo("asia;sana;jees");
    assertThat(iv.getReferencePeriodStart().orElse(null)).isEqualTo(LocalDate.of(2017, 7, 1));
    assertThat(iv.getReferencePeriodEnd().orElse(null)).isEqualTo(LocalDate.of(2017, 7, 30));
    assertThat(iv.getValueDomainType().orElse(null)).isEqualTo("enumerated");
    assertThat(iv.getMissingValues().get("fi")).isEqualTo("Joitain arvoja saattaa puuttua.\r\rTässä kentässä on myös rivivaihtoja.");
    assertThat(iv.getDefaultMissingValue().orElse(null)).isEqualTo("-1");
    assertThat(iv.getQualityStatement().get("fi")).isEqualTo("Ei ole muita laatuun vaikuttavia tietoja.");
    assertThat(iv.getSourceDescription().get("fi")).isEqualTo("Väestörekisteri");
    assertThat(iv.getDataType().orElse(null)).isEqualTo("integer");
  }

  @Test
  public void csvWithPrefLabelAndDescriptionOnly() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-description-and-prefLabel-only.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "MacRoman", codeListService).parse();

    assertThat(results.getParsedObject().get()).hasSize(1);
    InstanceVariable iv = results.getParsedObject().get().iterator().next().getParsedObject().get();
    assertThat(iv.getPrefLabel().get("fi")).isEqualTo("Nimi");
    assertThat(iv.getDescription().get("fi")).isEqualTo("Tässä on kuvausteksti.");
  }

  @Test
  public void missingPrefLabelColumn() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-no-prefLabel-column.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "MacRoman", codeListService).parse();

    assertThat(results.getParsedObject().get()).isEmpty();
    assertThat(results.getMessages()).containsExactly("import.csv.error.missingRequiredColumn.prefLabel");
  }

  @Test
  public void missingPrefLabelOnSingleRow() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-no-prefLabel-on-single-row.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "MacRoman", codeListService).parse();

    assertThat(results.getParsedObject().get()).hasSize(2);
    ParsingResult<InstanceVariable> firstResult = results.getParsedObject().get().iterator().next();
    assertFalse(firstResult.getParsedObject().isPresent());
    assertThat(firstResult.getMessages()).containsExactly("import.csv.error.missingRequiredValue.prefLabel");
  }

  @Test
  public void invalidReferencePeriod() throws Exception {
    InputStream csv = getClass().getResourceAsStream("/csv/instance-variables-invalid-reference-period.csv");

    ParsingResult<List<ParsingResult<InstanceVariable>>> results = new InstanceVariableCsvParser(csv, "MacRoman", codeListService).parse();

    assertThat(results.getParsedObject().get()).hasSize(1);
    ParsingResult<InstanceVariable> firstResult = results.getParsedObject().get().iterator().next();
    assertTrue(firstResult.getParsedObject().isPresent());
    assertThat(firstResult.getMessages()).containsExactly("import.csv.warn.invalidIsoDate.referencePeriodStart",
      "import.csv.warn.invalidIsoDate.referencePeriodEnd");
  }

}
