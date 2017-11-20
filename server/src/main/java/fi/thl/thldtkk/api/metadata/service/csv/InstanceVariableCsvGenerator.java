package fi.thl.thldtkk.api.metadata.service.csv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.csv.serialize.AdditionalCsvField;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class InstanceVariableCsvGenerator {

  private Logger log = LoggerFactory.getLogger(getClass());

  private List<InstanceVariable> instanceVariables;
  private Locale locale;
  private String encoding;
  private Map<String, AdditionalCsvField> additionalCsvFields;

  private static final String DEFAULT_ENCODING = "ISO-8859-15";

  public InstanceVariableCsvGenerator(List<InstanceVariable> instanceVariables, String language, String encoding) {
    this.instanceVariables = instanceVariables;
    this.locale = new Locale(language);
    this.encoding = StringUtils.hasText(encoding) ? encoding : DEFAULT_ENCODING;

    additionalCsvFields = new LinkedHashMap<>();

    Arrays.asList(AdditionalCsvField.values())
            .forEach(additionalField -> additionalCsvFields.put(additionalField.getSourceField(), additionalField));
  }

  public GeneratorResult generate() {

    CsvMapper mapper = new CsvMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new JavaTimeModule());
    mapper.addMixIn(InstanceVariable.class, InstanceVariableCsvExportFormat.class);
    mapper.configure(CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING, true);
    mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
    mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

    CsvSchema instanceVariableSchema = buildSchema(mapper);

    ObjectWriter writer = mapper
            .writerFor(InstanceVariable.class)
            .with(instanceVariableSchema)
            .with(locale);

    GeneratorResult result = new GeneratorResult(Optional.empty());

    try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bytesOut, encoding))) {

      writer.writeValues(bw).writeAll(instanceVariables);
      result = new GeneratorResult(Optional.of(bytesOut.toByteArray()));

    } catch (IOException ex) {
      log.error("CSV generation failed for instance variables", ex);
    }

    return result;
  }

  private CsvSchema buildSchema(CsvMapper mapper) {
    CsvSchema instanceVariableSchema = mapper.schemaFor(InstanceVariable.class);

    CsvSchema.Builder schemaBuilder = CsvSchema.emptySchema().rebuild();
    schemaBuilder.setUseHeader(true);
    schemaBuilder.setColumnSeparator(';');

    Iterator<CsvSchema.Column> columnIterator = instanceVariableSchema.iterator();

    while(columnIterator.hasNext()) {
      CsvSchema.Column column = columnIterator.next();
        schemaBuilder.addColumn(column);

        if(this.additionalCsvFields.containsKey(column.getName())) {
          List<String> additionalFields = additionalCsvFields.get(column.getName()).getAdditionalFields();
          additionalFields.forEach(field -> schemaBuilder.addColumn(field));
        }
    }

    CsvSchema augmentedInstanceVariableSchema = schemaBuilder.build();
    return augmentedInstanceVariableSchema;
  }


}
