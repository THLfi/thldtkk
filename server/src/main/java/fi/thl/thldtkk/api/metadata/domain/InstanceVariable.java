package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;

import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class InstanceVariable implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "InstanceVariable";
  public static final String VALUE_DOMAIN_TYPE_DESCRIBED = "described";
  public static final String VALUE_DOMAIN_TYPE_ENUMERATED = "enumerated";

  private UUID id;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Map<String, String> freeConcepts = new LinkedHashMap<>();
  private Boolean published;
  private LocalDate referencePeriodStart;
  private LocalDate referencePeriodEnd;
  private String technicalName;
  private String valueDomainType;
  private Quantity quantity;
  private Unit unit;
  private CodeList codeList;
  private List<Concept> conceptsFromScheme = new ArrayList<>();
  private Map<String, String> qualityStatement = new LinkedHashMap<>();
  private Map<String, String> missingValues = new LinkedHashMap<>();
  private String defaultMissingValue;
  private BigDecimal valueRangeMax;
  private BigDecimal valueRangeMin;
  private Dataset source;
  private Map<String, String> sourceDescription = new LinkedHashMap<>();
  private Variable variable;
  private Map<String, String> partOfGroup = new LinkedHashMap<>();
  private String dataType;
  private UnitType unitType;
  private Map<String, String> dataFormat = new LinkedHashMap<>();
  private List<InstanceQuestion> instanceQuestions = new ArrayList<>();
  private Dataset dataset;
  private Date lastModifiedDate;
  private UserProfile lastModifiedByUser;

  public InstanceVariable() {

  }

  public InstanceVariable(UUID id) {
    this.id = requireNonNull(id);
  }

  public InstanceVariable(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.freeConcepts = toLangValueMap(node.getProperties("freeConcepts"));
    this.published = toBoolean(node.getProperties("published"), false);
    this.referencePeriodStart = toLocalDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toLocalDate(node.getProperties("referencePeriodEnd"), null);
    this.technicalName = PropertyMappings.toString(node.getProperties("technicalName"));
    this.valueDomainType = PropertyMappings.toString(node.getProperties("valueDomainType"));
    this.qualityStatement = toLangValueMap(node.getProperties("qualityStatement"));
    this.missingValues = toLangValueMap(node.getProperties("missingValues"));
    this.defaultMissingValue = PropertyMappings.toString(node.getProperties("defaultMissingValue"));
    this.valueRangeMax = PropertyMappings.toBigDecimal(node.getProperties("valueRangeMax"), null);
    this.valueRangeMin = PropertyMappings.toBigDecimal(node.getProperties("valueRangeMin"), null);
    this.partOfGroup = toLangValueMap(node.getProperties("partOfGroup"));
    this.sourceDescription = toLangValueMap(node.getProperties("sourceDescription"));
    this.dataType = PropertyMappings.toString(node.getProperties("dataType"));
    this.dataFormat = toLangValueMap(node.getProperties("dataFormat"));
    this.lastModifiedDate = node.getLastModifiedDate();

    node.getReferences("quantity")
        .stream().findFirst().ifPresent(quantity -> this.quantity = new Quantity(quantity));
    node.getReferences("unit")
        .stream().findFirst().ifPresent(unit -> this.unit = new Unit(unit));
    node.getReferences("codeList")
        .stream().findFirst().ifPresent(codeList -> this.codeList = new CodeList(codeList));
    node.getReferences("conceptsFromScheme")
        .forEach(c -> this.conceptsFromScheme.add(new Concept(c)));
    node.getReferencesFirst("variable")
        .ifPresent(v -> this.variable = new Variable(v));
    node.getReferences("source")
        .stream().findFirst().ifPresent(source -> this.source = new Dataset(source));
    node.getReferences("unitType")
        .stream().findFirst().ifPresent(unitType -> this.unitType = new UnitType(unitType));
    node.getReferences("instanceQuestions")
    .forEach(q -> this.instanceQuestions.add(new InstanceQuestion(q)));

    node.getReferrers("instanceVariable")
        .stream().findFirst().ifPresent(dataset -> this.dataset = new Dataset(dataset));
    node.getReferencesFirst("lastModifiedByUser")
        .ifPresent(v -> this.lastModifiedByUser = new UserInformation(new UserProfile(v)));
  }

  /**
   * Constructor for testing purposes
   */
  public InstanceVariable(UUID id,
        Map<String, String> prefLabel,
        Map<String, String> description,
        Boolean published,
        LocalDate referencePeriodStart,
        LocalDate referencePeriodEnd,
        String technicalName,
        String valueDomainType,
        Map<String, String> qualityStatement,
        Map<String, String> missingValues,
        String defaultMissingValue,
        Dataset source,
        Map<String, String> sourceDescription,
        Map<String, String> partOfGroup,
        String dataType,
        Map<String, String> dataFormat,
        Dataset dataset,
        Unit unit,
        Quantity quantity,
        CodeList codeList,
        BigDecimal valueRangeMin,
        BigDecimal valueRangeMax,
        UnitType unitType,
        Variable variable,
        Map<String, String> freeConcepts,
        List<Concept> conceptsFromScheme,
        List<InstanceQuestion> instanceQuestions) {
    this.id = id;
    this.prefLabel = prefLabel;
    this.description = description;
    this.published = published;
    this.referencePeriodStart = referencePeriodStart;
    this.referencePeriodEnd = referencePeriodEnd;
    this.technicalName = technicalName;
    this.valueDomainType = valueDomainType;
    this.qualityStatement = qualityStatement;
    this.defaultMissingValue = defaultMissingValue;
    this.missingValues = missingValues;
    this.source = source;
    this.sourceDescription = sourceDescription;
    this.dataType = dataType;
    this.dataFormat = dataFormat;
    this.dataset = dataset;
    this.partOfGroup = partOfGroup;
    this.unit = unit;
    this.quantity = quantity;
    this.codeList = codeList;
    this.valueRangeMin = valueRangeMin;
    this.valueRangeMax = valueRangeMax;
    this.unitType = unitType;
    this.variable = variable;
    this.freeConcepts = freeConcepts;
    this.conceptsFromScheme = conceptsFromScheme;
    this.instanceQuestions = instanceQuestions;
  }

  public Optional<String> getDefaultMissingValue() {
    return Optional.ofNullable(defaultMissingValue);
  }

  public void setDefaultMissingValue(String defaultMissingValue) {
    this.defaultMissingValue = defaultMissingValue;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Map<String, String> getMissingValues() {
    return missingValues;
  }

  public Map<String, String> getPartOfGroup() {
    return partOfGroup;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public Map<String, String> getQualityStatement() {
    return qualityStatement;
  }

  public Optional<LocalDate> getReferencePeriodStart() {
    return Optional.ofNullable(referencePeriodStart);
  }

  public void setReferencePeriodStart(LocalDate referencePeriodStart) {
    this.referencePeriodStart = referencePeriodStart;
  }

  public Optional<LocalDate> getReferencePeriodEnd() {
    return Optional.ofNullable(referencePeriodEnd);
  }

  public void setReferencePeriodEnd(LocalDate referencePeriodEnd) {
    this.referencePeriodEnd = referencePeriodEnd;
  }

  public Optional<Dataset> getSource() {
    return Optional.ofNullable(source);
  }

  public void setSource(Dataset source) {
    this.source = source;
  }

  public Map<String, String> getSourceDescription() {
    return sourceDescription;
  }

  public Optional<String> getTechnicalName() {
    return Optional.ofNullable(technicalName);
  }

  public void setTechnicalName(String technicalName) {
    this.technicalName = technicalName;
  }

  public Optional<UnitType> getUnitType() {
    return Optional.ofNullable(unitType);
  }

  public Optional<String> getValueDomainType() {
    return Optional.ofNullable(valueDomainType);
  }

  public void setValueDomainType(String valueDomainType) {
    this.valueDomainType = valueDomainType;
  }

  public Optional<String> getDataType() {
    return Optional.ofNullable(dataType);
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public Optional<Quantity> getQuantity() {
    return Optional.ofNullable(quantity);
  }

  public void setQuantity(Quantity quantity) {
    this.quantity = quantity;
  }

  public Optional<Unit> getUnit() {
    return Optional.ofNullable(unit);
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  public Optional<CodeList> getCodeList() {
    return Optional.ofNullable(codeList);
  }

  public void setCodeList(CodeList codeList) {
    this.codeList = codeList;
  }

  public List<Concept> getConceptsFromScheme() {
    return conceptsFromScheme;
  }

  public Map<String, String> getFreeConcepts() {
    return freeConcepts;
  }

  public Optional<Boolean> isPublished() {
    return Optional.ofNullable(published);
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public Optional<BigDecimal> getValueRangeMax() {
    return Optional.ofNullable(valueRangeMax);
  }

  public void setValueRangeMax(BigDecimal valueRangeMax) {
    this.valueRangeMax = valueRangeMax;
  }

  public Optional<BigDecimal> getValueRangeMin() {
    return Optional.ofNullable(valueRangeMin);
  }

  public void setValueRangeMin(BigDecimal valueRangeMin) {
    this.valueRangeMin = valueRangeMin;
  }

  public Optional<Variable> getVariable() {
    return Optional.ofNullable(variable);
  }

  public List<InstanceQuestion> getInstanceQuestions() {
    return instanceQuestions;
  }

  public Map<String, String> getDataFormat() {
    return dataFormat;
  }

  public Optional<Dataset> getDataset() {
    return Optional.ofNullable(dataset);
  }

  public void setDataset(Dataset dataset) {
    this.dataset = dataset;
  }

  public Optional<Study> getStudy() {
    return dataset != null ? dataset.getStudy() : Optional.empty();
  }

  public Optional<UnitType> getDatasetUnitType() {
    return dataset != null ? dataset.getUnitType() : Optional.empty();
  }

  public Optional<UnitType> getStudyUnitType() {
    return dataset != null ? dataset.getStudy().flatMap(Study::getUnitType) : Optional.empty();
  }

  public Optional<Date> getLastModifiedDate() {
    return Optional.ofNullable(lastModifiedDate);
  }

  public Optional<UserProfile> getLastModifiedByUser() {
    return Optional.ofNullable(lastModifiedByUser);
  }

  public void setLastModifiedByUser(UserProfile userProfile) {
    this.lastModifiedByUser = userProfile;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("description", toPropertyValues(description));
    props.putAll("freeConcepts", toPropertyValues(freeConcepts));
    props.putAll("qualityStatement", toPropertyValues(qualityStatement));
    props.putAll("missingValues", toPropertyValues(missingValues));
    props.putAll("partOfGroup", toPropertyValues(partOfGroup));
    props.putAll("sourceDescription", toPropertyValues(sourceDescription));
    props.putAll("dataFormat", toPropertyValues(dataFormat));

    isPublished().ifPresent(v -> props.put("published", toPropertyValue(v)));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));
    getTechnicalName().ifPresent((v -> props.put("technicalName", toPropertyValue(v))));
    getValueDomainType().ifPresent((v -> props.put("valueDomainType", toPropertyValue(v))));
    getDefaultMissingValue().ifPresent((v -> props.put("defaultMissingValue", toPropertyValue(v))));
    getValueRangeMax().ifPresent((v -> props.put("valueRangeMax", toPropertyValue(v))));
    getValueRangeMin().ifPresent((v -> props.put("valueRangeMin", toPropertyValue(v))));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getQuantity().ifPresent(quantity -> refs.put("quantity", quantity.toNode()));
    getUnit().ifPresent(unit -> refs.put("unit", unit.toNode()));
    getCodeList().ifPresent(codeList -> refs.put("codeList", codeList.toNode()));
    getConceptsFromScheme().forEach(c -> refs.put("conceptsFromScheme", c.toNode()));
    getSource().ifPresent(source -> refs.put("source", source.toNode()));
    getDataType().ifPresent((v -> props.put("dataType", toPropertyValue(v))));
    getVariable().ifPresent((v -> refs.put("variable", v.toNode())));
    getUnitType().ifPresent((ut -> refs.put("unitType", ut.toNode())));
    getInstanceQuestions().forEach(q -> refs.put("instanceQuestions", q.toNode()));

    Multimap<String, Node> referrers = LinkedHashMultimap.create();
    getDataset().ifPresent((v -> referrers.put("dataset", v.toNode())));
    getLastModifiedByUser().ifPresent(v -> refs.put("lastModifiedByUser", v.toNode()));

    return new Node(id, TERMED_NODE_CLASS, props, refs, referrers);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstanceVariable that = (InstanceVariable) o;
    return Objects.equals(id, that.id)
        && Objects.equals(prefLabel, that.prefLabel)
        && Objects.equals(description, that.description)
        && Objects.equals(referencePeriodStart, that.referencePeriodStart)
        && Objects.equals(referencePeriodEnd, that.referencePeriodEnd)
        && Objects.equals(technicalName, that.technicalName)
        && Objects.equals(valueDomainType, that.valueDomainType)
        && Objects.equals(quantity, that.quantity)
        && Objects.equals(unit, that.unit)
        && Objects.equals(codeList, that.codeList)
        && Objects.equals(conceptsFromScheme, that.conceptsFromScheme)
        && Objects.equals(freeConcepts, that.freeConcepts)
        && Objects.equals(qualityStatement, that.qualityStatement)
        && Objects.equals(missingValues, that.missingValues)
        && Objects.equals(defaultMissingValue, that.defaultMissingValue)
        && Objects.equals(valueRangeMax, that.valueRangeMax)
        && Objects.equals(valueRangeMin, that.valueRangeMin)
        && Objects.equals(partOfGroup, that.partOfGroup)
        && Objects.equals(source, that.source)
        && Objects.equals(sourceDescription, that.sourceDescription)
        && Objects.equals(valueRangeMin, that.valueRangeMin)
        && Objects.equals(variable, that.variable)
        && Objects.equals(dataType, that.dataType)
        && Objects.equals(unitType, that.unitType)
        && Objects.equals(instanceQuestions, that.instanceQuestions)
        && Objects.equals(published, that.published)
        && Objects.equals(dataFormat, that.dataFormat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, description, referencePeriodStart, lastModifiedDate,
        referencePeriodEnd, technicalName, valueDomainType, quantity, unit, codeList,
        conceptsFromScheme, freeConcepts, qualityStatement, missingValues,
        defaultMissingValue, valueRangeMax, valueRangeMin, partOfGroup, variable,
        source, sourceDescription, dataType, unitType, instanceQuestions, published, dataFormat);
  }

  @JsonIgnore
  public InstanceVariable getSimplified() {
    InstanceVariable instanceVariable = new InstanceVariable();
    instanceVariable.id = this.id;
    instanceVariable.prefLabel = this.prefLabel;
    instanceVariable.description = this.description;
    instanceVariable.referencePeriodStart = this.referencePeriodStart;
    instanceVariable.referencePeriodEnd = this.referencePeriodEnd;
    instanceVariable.technicalName = this.technicalName;
    instanceVariable.valueDomainType = this.valueDomainType;
    instanceVariable.quantity = this.quantity;
    instanceVariable.unit = this.unit;
    instanceVariable.codeList = this.codeList;
    instanceVariable.conceptsFromScheme = this.conceptsFromScheme;
    instanceVariable.freeConcepts = this.freeConcepts;
    instanceVariable.qualityStatement = this.qualityStatement;
    instanceVariable.missingValues = this.missingValues;
    instanceVariable.defaultMissingValue = this.defaultMissingValue;
    instanceVariable.valueRangeMax = this.valueRangeMax;
    instanceVariable.valueRangeMin = this.valueRangeMin;
    instanceVariable.partOfGroup = this.partOfGroup;
    instanceVariable.source = this.source;
    instanceVariable.sourceDescription = this.sourceDescription;
    instanceVariable.valueRangeMin = this.valueRangeMin;
    instanceVariable.variable = this.variable;
    instanceVariable.dataType = this.dataType;
    instanceVariable.unitType = this.unitType;
    instanceVariable.instanceQuestions = this.instanceQuestions;
    instanceVariable.published = this.published;
    instanceVariable.dataFormat = this.dataFormat;
    return instanceVariable;
  }
}
