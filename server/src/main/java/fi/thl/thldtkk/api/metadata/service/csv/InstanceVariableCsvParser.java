package fi.thl.thldtkk.api.metadata.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InstanceVariableCsvParser {

  private static final Logger LOG = LoggerFactory.getLogger(InstanceVariableCsvParser.class);

  private final InputStream csv;
  private final String encoding;

  private final List<ParsingResult<InstanceVariable>> results;
  private final List<String> messages;

  public InstanceVariableCsvParser(InputStream csv, String encoding) {
    this.csv = csv;
    this.encoding = encoding;
    this.results = new LinkedList<>();
    this.messages = new LinkedList<>();
  }

  public ParsingResult<List<ParsingResult<InstanceVariable>>> parse() {
    if (!StringUtils.hasText(encoding)) {
      messages.add("import.csv.error.noEncoding");
      return done();
    }

    CsvMapper mapper = new CsvMapper();
    CsvSchema schema = CsvSchema.emptySchema()
      .withHeader()
      .withColumnSeparator(';');
    ObjectReader objectReader = mapper.readerFor(Map.class)
      .with(schema)
      .withFeatures(CsvParser.Feature.TRIM_SPACES);

    MappingIterator<Map<String, String>> iterator;
    try {
      InputStreamReader csvReader = new InputStreamReader(csv, encoding);
      iterator = objectReader.readValues(csvReader);
    }
    catch (UnsupportedEncodingException e) {
      LOG.warn("Invalid encoding: {}", encoding, e);
      messages.add("import.csv.error.unsupportedEncoding");
      return done();
    }
    catch (IOException e) {
      LOG.warn("Failed to read CSV", e);
      messages.add("import.csv.error.unknown");
      return done();
    }

    boolean firstRow = true;
    while (iterator.hasNext()) {
      Map<String,String> row = iterator.next();

      if (firstRow) {
        if (!row.keySet().contains("prefLabel")) {
          messages.add("import.csv.error.missingRequiredColumn.prefLabel");
          return done();
        }
        firstRow = false;
      }

      handleRow(row);
    }

    return done();
  }

  private ParsingResult<List<ParsingResult<InstanceVariable>>> done() {
    return new ParsingResult<>(results, messages);
  }

  private void handleRow(Map<String, String> row) {
    final String language = "fi";
    InstanceVariable instanceVariable = new InstanceVariable();
    List<String> rowMessages = new LinkedList<>();

    boolean isRowValid = true;

    String prefLabel = row.get("prefLabel");
    if (StringUtils.hasText(prefLabel)) {
      instanceVariable.getPrefLabel().put(language, prefLabel);
    }
    else {
      isRowValid = false;
      rowMessages.add("import.csv.error.missingRequiredValue.prefLabel");
    }

    instanceVariable.setTechnicalName(row.get("technicalName"));
    instanceVariable.getDescription().put(language, row.get("description"));
    instanceVariable.getPartOfGroup().put(language, row.get("partOfGroup"));
    instanceVariable.getFreeConcepts().put(language, row.get("freeConcepts"));
    instanceVariable.setReferencePeriodStart(parseLocalDate(row, "referencePeriodStart", rowMessages));
    instanceVariable.setReferencePeriodEnd(parseLocalDate(row, "referencePeriodEnd", rowMessages));
    instanceVariable.setValueDomainType(row.get("valueDomainType"));
    instanceVariable.getMissingValues().put(language, row.get("missingValues"));
    instanceVariable.setDefaultMissingValue(row.get("defaultMissingValue"));
    instanceVariable.getQualityStatement().put(language, row.get("qualityStatement"));
    instanceVariable.getSourceDescription().put(language, row.get("sourceDescription"));
    instanceVariable.setDataType(row.get("dataType"));

    results.add(new ParsingResult<>(isRowValid ? instanceVariable : null, rowMessages));
  }

  private LocalDate parseLocalDate(Map<String, String> row, String field, List<String> rowMessages) {
    String dateString = row.get(field);
    LocalDate date = null;
    if (StringUtils.hasText(dateString)) {
      try {
        date = LocalDate.parse(dateString);
      }
      catch (DateTimeParseException e) {
        rowMessages.add("import.csv.warn.invalidIsoDate." + field);
      }
    }
    return date;
  }

}
