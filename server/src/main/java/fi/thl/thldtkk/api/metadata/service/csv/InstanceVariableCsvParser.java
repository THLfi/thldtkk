package fi.thl.thldtkk.api.metadata.service.csv;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.service.csv.exception.UndefinedLabelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class InstanceVariableCsvParser {

  private static final Logger LOG = LoggerFactory.getLogger(InstanceVariableCsvParser.class);

  private final InputStream csv;
  private final String encoding;

  private final List<ParsingResult<InstanceVariable>> results;
  private final List<String> messages;
  
  private Service<UUID, CodeList> codeListService;
  
  public InstanceVariableCsvParser(InputStream csv, String encoding, Service<UUID, CodeList> codeListService) {
    this.csv = csv;
    this.encoding = encoding;
    this.results = new LinkedList<>();
    this.messages = new LinkedList<>();
    
    this.codeListService = codeListService;
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

    sanitize(row.get("technicalName")).ifPresent(technicalName -> instanceVariable.setTechnicalName(technicalName));
    sanitize(row.get("description")).ifPresent(description -> instanceVariable.getDescription().put(language,description));
    sanitize(row.get("partOfGroup")).ifPresent(partOfGroup -> instanceVariable.getPartOfGroup().put(language, partOfGroup));
    sanitize(row.get("freeConcepts")).ifPresent(freeConcepts -> instanceVariable.getFreeConcepts().put(language, freeConcepts));
    
    instanceVariable.setReferencePeriodStart(parseLocalDate(row, "referencePeriodStart", rowMessages));
    instanceVariable.setReferencePeriodEnd(parseLocalDate(row, "referencePeriodEnd", rowMessages));
    
    sanitize(row.get("valueDomainType")).ifPresent(valueDomainType -> instanceVariable.setValueDomainType(valueDomainType));
    sanitize(row.get("missingValues")).ifPresent((missingValues -> instanceVariable.getMissingValues().put(language, missingValues)));
    sanitize(row.get("defaultMissingValue")).ifPresent(defaultMissingValue -> instanceVariable.setDefaultMissingValue(defaultMissingValue));
    sanitize(row.get("qualityStatement")).ifPresent(qualityStatement -> instanceVariable.getQualityStatement().put(language, qualityStatement));
    
    sanitize(row.get("sourceDescription")).ifPresent(sourceDescription -> instanceVariable.getSourceDescription().put(language, sourceDescription));
    sanitize(row.get("dataType")).ifPresent(dataType -> instanceVariable.setDataType(dataType));
    sanitize(row.get("dataFormat")).ifPresent(dataFormat -> instanceVariable.getDataFormat().put(language, dataFormat));
    
    Optional<String> codeListPrefLabel = sanitize(row.get("codeList.prefLabel"));
    Optional<String> codeListReferenceId = sanitize(row.get("codeList.referenceId"));

    if((codeListPrefLabel.isPresent() && !StringUtils.isEmpty(codeListPrefLabel.get())) 
            || (codeListReferenceId.isPresent() && !StringUtils.isEmpty(codeListReferenceId.get()))) {

      Optional<String> codeListDescription = sanitize(row.get("codeList.description"));
      Optional<String> codeListOwner = sanitize(row.get("codeList.owner"));
      
      try {
          Optional<CodeList> codeList = getCodeList(codeListPrefLabel, codeListReferenceId, language, rowMessages);
          if(!codeList.isPresent()) {
              codeList = createCodeList(codeListPrefLabel, language, codeListReferenceId, codeListDescription, codeListOwner);
          }
          codeList.ifPresent(cl -> {
                  instanceVariable.setCodeList(cl);
                  instanceVariable.setValueDomainType(InstanceVariable.VALUE_DOMAIN_TYPE_ENUMERATED);
                });
      }
      
      catch(UndefinedLabelException e) {
          isRowValid = false;
          rowMessages.add("import.csv.error.missingRequiredValue.codeList.prefLabel");
      }

    }
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
  
  private Optional<String> sanitize(String columnEntry) {
    return Optional.ofNullable(columnEntry)
            .map(String::trim)
            .map(this::normalizeLineBreaks);
  }
  
  private String normalizeLineBreaks(String columnEntry) {    
    String windowsStyleLineBreaksReplaced = columnEntry.replaceAll("\r\n", "\n"); // CR+LF -> LF
    String linefeedsOnlyReplaced = windowsStyleLineBreaksReplaced.replaceAll("\r", "\n"); // CR -> LF
    return linefeedsOnlyReplaced;
  }
  
  private List<CodeList> searchCodeListsByReferenceId(String referenceId) {
      return codeListService.query(referenceId).filter(codeList -> {
              Optional<String> storedReferenceId = codeList.getReferenceId();
              return storedReferenceId.isPresent() ? storedReferenceId.get().equalsIgnoreCase(referenceId) : false;
          }).collect(Collectors.toList());
  }
  
  private List<CodeList> searchCodeListsByLabel(String label, String language) {
      return codeListService.query(label).filter(codeList -> {
          String storedLabel = codeList.getPrefLabel().get(language);
          return storedLabel != null ? storedLabel.equalsIgnoreCase(label) : false;
      }).collect(Collectors.toList());
  }
  
  private Optional<CodeList> getCodeList(Optional<String> label, Optional<String> referenceId, String language, List<String> rowMessages) {
                  
      List<CodeList> bestMatches = new ArrayList<>();
      List<CodeList> codeListsByLabel = new ArrayList<>();
      List<CodeList> codeListsByReferenceId = new ArrayList<>();
            
      if(label.isPresent() && !StringUtils.isEmpty(label.get())) {
         codeListsByLabel = searchCodeListsByLabel(label.get(), language);
      }
      
      if(referenceId.isPresent() && !StringUtils.isEmpty(referenceId.get())) {
         codeListsByReferenceId = searchCodeListsByReferenceId(referenceId.get());
      }
      
      // match order (1.) label and ref. id; (2.) ref. id only; (3.) label only
      
      if(!codeListsByLabel.isEmpty() && !codeListsByReferenceId.isEmpty()) {
          for(CodeList referenceIdCodeList : codeListsByReferenceId) {
              if(codeListsByLabel.contains(referenceIdCodeList)) {
                  bestMatches.add(referenceIdCodeList);
              }
          }
      }
      
      if(bestMatches.isEmpty()) {
          if(!codeListsByReferenceId.isEmpty()) {
              bestMatches = codeListsByReferenceId;
          }
          
          else if (!codeListsByLabel.isEmpty()) {
              bestMatches = codeListsByLabel;
          }
      }
      
      if(bestMatches.size() > 1) {
          rowMessages.add("import.csv.warn.ambigiousCodeList"); // should fail import?
      }
      
      Optional<CodeList> existingCodeList = bestMatches.size() >= 1 ? Optional.of(bestMatches.get(0)) : Optional.empty();
      return existingCodeList;
  }

  private Optional<CodeList> createCodeList(Optional<String> label, String language, Optional<String> referenceId, Optional<String> description, Optional<String> owner) throws UndefinedLabelException {
      
      if(!label.isPresent() || (label.isPresent() && StringUtils.isEmpty(label.get())) ) {
          throw new UndefinedLabelException();
      }
      
      CodeList codeList = new CodeList();
      codeList.getPrefLabel().put(language, label.get());
      
      codeList.setCodeListType(CodeList.CODE_LIST_TYPE_EXTERNAL);
      
      referenceId.ifPresent(refId -> codeList.setReferenceId(refId));
      description.ifPresent(localizedDescription -> codeList.getDescription().put(language, localizedDescription));
      owner.ifPresent(localizedOwner -> codeList.getOwner().put(language, localizedOwner));
      
      return Optional.ofNullable(codeListService.save(codeList));
  }
  
}
