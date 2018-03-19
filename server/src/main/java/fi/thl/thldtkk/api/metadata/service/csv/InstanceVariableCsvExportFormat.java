package fi.thl.thldtkk.api.metadata.service.csv;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.thl.thldtkk.api.metadata.domain.*;
import fi.thl.thldtkk.api.metadata.service.csv.serialize.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@JsonPropertyOrder(value = {"prefLabel", "id", "technicalName", "description",
  "dataset", "datasetUnitType", "study", "studyUnitType", "freeConcepts", "conceptsFromScheme",
  "referencePeriodStart", "referencePeriodEnd", "partOfGroup", "published",
  "unitType", "valueDomainType", "dataFormat", "dataType", "missingValues",
  "defaultMissingValue", "qualityStatement", "quantity", "unit",
  "valueRangeMin", "valueRangeMax", "codeList", "instanceQuestions",
  "source", "sourceDescription", "variable", "lastModifiedDate"})
public interface InstanceVariableCsvExportFormat {

  public Optional<String> getTechnicalName();

  public Optional<String> getDataType();

  @JsonProperty("unit.prefLabel")
  @JsonSerialize(using = UnitSerializer.class)
  public Optional<Unit> getUnit();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getPrefLabel();

  @JsonProperty("codeList.prefLabel")
  @JsonSerialize(using = CodeListSerializer.class)
  public Optional<CodeList> getCodeList();

  @JsonSerialize(using = ConceptSerializer.class)
  public List<Concept> getConceptsFromScheme();

  @JsonProperty("dataset.prefLabel")
  @JsonSerialize(using = InstanceVariableDatasetSerializer.class)
  public Optional<Dataset> getDataset();

  @JsonProperty("dataset.unitType.prefLabel")
  @JsonSerialize(using = DatasetUnitTypeSerializer.class)
  public Optional<UnitType> getDatasetUnitType();

  @JsonProperty("study.prefLabel")
  @JsonSerialize(using = StudySerializer.class)
  public Optional<Study> getStudy();

  @JsonProperty("study.unitType.prefLabel")
  @JsonSerialize(using = StudyUnitTypeSerializer.class)
  public Optional<UnitType> getStudyUnitType();

  public Optional<String> getDefaultMissingValue();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getDescription();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getFreeConcepts();

  public UUID getId();

  @JsonSerialize(using = InstanceQuestionSerializer.class)
  public List<InstanceQuestion> getInstanceQuestions();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getMissingValues();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getPartOfGroup();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getQualityStatement();

  @JsonSerialize(using = QuantitySerializer.class)
  @JsonProperty("quantity.prefLabel")
  public Optional<Quantity> getQuantity();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public Optional<LocalDate> getReferencePeriodEnd();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public Optional<LocalDate> getReferencePeriodStart();

  @JsonProperty("source.dataset.prefLabel")
  @JsonSerialize(using = SourceDatasetSerializer.class)
  public Optional<Dataset> getSource();

  @JsonProperty("source.description")
  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getSourceDescription();

  @JsonProperty("unitType.prefLabel")
  @JsonSerialize(using = UnitTypeSerializer.class)
  public Optional<UnitType> getUnitType();

  public Optional<String> getValueDomainType();

  public Optional<BigDecimal> getValueRangeMax();

  public Optional<BigDecimal> getValueRangeMin();

  @JsonProperty("variable.prefLabel")
  @JsonSerialize (using = VariableSerializer.class)
  public Optional<Variable> getVariable();

  @JsonProperty(value = "published")
  @JsonSerialize(using = OptionalToStringSerializer.class)
  public Optional<Boolean> isPublished();

  @JsonSerialize(using = LangValueSerializer.class)
  public Map<String,String> getDataFormat();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  public Optional<Date> getLastModifiedDate();

  @JsonIgnore
  public Optional<UserProfile> getLastModifiedByUser();

}
