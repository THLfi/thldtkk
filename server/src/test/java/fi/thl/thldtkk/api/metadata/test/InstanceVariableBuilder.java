package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Quantity;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.Variable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;


public class InstanceVariableBuilder {
  
  private UUID id;
  private Map<String, String> prefLabel;
  private Map<String, String> description = new LinkedHashMap<>();
  private Boolean published;
  private LocalDate referencePeriodStart;
  private LocalDate referencePeriodEnd;
  private String technicalName;
  private String valueDomainType;
  private Map<String, String> qualityStatement = new LinkedHashMap<>();
  private Map<String, String> missingValues = new LinkedHashMap<>();
  private String defaultMissingValue;
  private BigDecimal valueRangeMax;
  private BigDecimal valueRangeMin;
  private Dataset source;
  private Map<String, String> sourceDescription = new LinkedHashMap<>();
  private Map<String, String> partOfGroup = new LinkedHashMap<>();
  private String dataType;
  private Map<String, String> dataFormat = new LinkedHashMap<>();
  private Dataset dataset;
  private Unit unit;
  private Quantity quantity;
  private CodeList codeList;
  private UnitType unitType;
  private Variable variable;
  private Map<String, String> freeConcepts = new LinkedHashMap<>();
  private List<Concept> conceptsFromScheme = new ArrayList<>();
  private List<InstanceQuestion> instanceQuestions = new ArrayList<>();
    
  public InstanceVariableBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public InstanceVariableBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public InstanceVariableBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public InstanceVariableBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
  
  public InstanceVariableBuilder withDescription(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public InstanceVariableBuilder withDescription(String descriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", descriptionValue);
    return withDescription(langValues);
  }
  
  public InstanceVariableBuilder withPublished(Boolean published) {
    this.published = published;
    return this;
  }
  
  public InstanceVariableBuilder withReferencePeriodStart(LocalDate referencePeriodStart) {
    this.referencePeriodStart = referencePeriodStart;
    return this;
  }
  
  public InstanceVariableBuilder withReferencePeriodEnd(LocalDate referencePeriodEnd) {
    this.referencePeriodEnd = referencePeriodEnd;
    return this;
  }
  
  public InstanceVariableBuilder withTechnicalName(String technicalName) {
    this.technicalName = technicalName;
    return this;
  }
  
  public InstanceVariableBuilder withValueDomainType(String valueDomainType) {
    this.valueDomainType = valueDomainType;
    return this;
  }

  public InstanceVariableBuilder withQualityStatement(Map<String, String> qualityStatement) {
    this.qualityStatement = qualityStatement;
    return this;
  }
  
  public InstanceVariableBuilder withQualityStatement(String qualityStatementValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", qualityStatementValue);
    return withQualityStatement(langValues);
  }
  
  public InstanceVariableBuilder withMissingValues(Map<String, String> missingValues) {
    this.missingValues = missingValues;
    return this;
  }
  
  public InstanceVariableBuilder withMissingValues(String missingValuesValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", missingValuesValue);
    return withMissingValues(langValues);
  }
  
  public InstanceVariableBuilder withDefaultMissingValue(String defaultMissingValue) {
    this.defaultMissingValue = defaultMissingValue;
    return this;
  }
  
  public InstanceVariableBuilder withSource(Dataset source) {
    this.source = source;
    return this;
  }
  
  public InstanceVariableBuilder withSourceDescription(Map<String, String> sourceDescription) {
    this.sourceDescription = sourceDescription;
    return this;
  }
  
  public InstanceVariableBuilder withSourceDescription(String sourceDescriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", sourceDescriptionValue);
    return withSourceDescription(langValues);
  }
  
   public InstanceVariableBuilder withPartOfGroup(Map<String, String> partOfGroup) {
    this.partOfGroup = partOfGroup;
    return this;
  }
  
  public InstanceVariableBuilder withPartOfGroup(String partOfGroupValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", partOfGroupValue);
    return withPartOfGroup(langValues);
  }
  
  public InstanceVariableBuilder withDataType(String dataType) {
    this.dataType = dataType;
    return this;
  }
  
  public InstanceVariableBuilder withDataFormat(Map<String, String> dataFormat) {
    this.dataFormat = dataFormat;
    return this;
  }
  
  public InstanceVariableBuilder withDataFormat(String dataFormatValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", dataFormatValue);
    return withDataFormat(langValues);
  }
  
  public InstanceVariableBuilder withDataSet(Dataset dataset) {
    this.dataset = dataset;
    return this;
  }
  
  public InstanceVariableBuilder withUnit(Unit unit) {
    this.unit = unit;
    return this;
  }
  
  public InstanceVariableBuilder withQuantity(Quantity quantity) {
    this.quantity = quantity;
    return this;
  }
  
  public InstanceVariableBuilder withCodeList(CodeList codeList) {
    this.codeList = codeList;
    return this;
  }
  
  public InstanceVariableBuilder withValueRangeMin(BigDecimal valueRangeMin) {
    this.valueRangeMin = valueRangeMin;
    return this;
  }
  
  public InstanceVariableBuilder withValueRangeMax(BigDecimal valueRangeMax) {
    this.valueRangeMax = valueRangeMax;
    return this;
  }
  
  public InstanceVariableBuilder withUnitType(UnitType unitType) {
    this.unitType = unitType;
    return this;
  }
  
  public InstanceVariableBuilder withVariable(Variable variable) {
    this.variable = variable;
    return this;
  }
  
  public InstanceVariableBuilder withFreeConcepts(Map<String, String> freeConcepts) {
    this.freeConcepts = freeConcepts;
    return this;
  }
  
  public InstanceVariableBuilder withConceptsFromScheme(List<Concept> conceptsFromScheme) {
    this.conceptsFromScheme = conceptsFromScheme;
    return this;
  }
  
  public InstanceVariableBuilder withInstanceQuestions(List<InstanceQuestion> instanceQuestions) {
    this.instanceQuestions = instanceQuestions;
    return this;
  }

  public InstanceVariable build() {
    return new InstanceVariable(
      id,
      prefLabel,
      description,
      published,
      referencePeriodStart,
      referencePeriodEnd,
      technicalName,
      valueDomainType,
      qualityStatement,
      missingValues,
      defaultMissingValue,
      source,
      sourceDescription,
      partOfGroup,
      dataType,
      dataFormat,
      dataset,
      unit,
      quantity,
      codeList,
      valueRangeMin,
      valueRangeMax,
      unitType,
      variable,
      freeConcepts,
      conceptsFromScheme,
      instanceQuestions
    );
  }
  
  
  
}
