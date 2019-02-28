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

// Match order of fields in UI with the exception that "variable" is not the first field
@JsonPropertyOrder(value = {
  "prefLabel",
  "technicalName",
  "description",
  "partOfGroup",
  "conceptsFromScheme",
  "freeConcepts",
  "referencePeriodStart",
  "referencePeriodEnd",
  "valueDomainType",
  "quantity",
  "unit",
  "valueRangeMin",
  "valueRangeMax",
  "codeList",
  "missingValues",
  "defaultMissingValue",
  "qualityStatement",
  "source",
  "sourceDescription",
  "dataType",
  "dataFormat",
  "unitType",
  "datasetUnitType",
  "studyUnitType",
  "instanceQuestions",
  "id",
  "dataset",
  "study",
  "published",
  "lastModifiedDate",
  "variable"
})
public interface InstanceVariableCsvExportFormat {

  Optional<String> getTechnicalName();

  Optional<String> getDataType();

  @JsonProperty("unit.prefLabel")
  @JsonSerialize(using = UnitSerializer.class)
  Optional<Unit> getUnit();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getPrefLabel();

  @JsonProperty("codeList.prefLabel")
  @JsonSerialize(using = CodeListSerializer.class)
  Optional<CodeList> getCodeList();

  @JsonSerialize(using = ConceptSerializer.class)
  List<Concept> getConceptsFromScheme();

  @JsonProperty("dataset.prefLabel")
  @JsonSerialize(using = InstanceVariableDatasetSerializer.class)
  Optional<Dataset> getDataset();

  @JsonProperty("dataset.unitType.prefLabel")
  @JsonSerialize(using = DatasetUnitTypeSerializer.class)
  Optional<UnitType> getDatasetUnitType();

  @JsonProperty("study.prefLabel")
  @JsonSerialize(using = StudySerializer.class)
  Optional<Study> getStudy();

  @JsonProperty("study.unitType.prefLabel")
  @JsonSerialize(using = StudyUnitTypeSerializer.class)
  Optional<UnitType> getStudyUnitType();

  Optional<String> getDefaultMissingValue();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getDescription();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getFreeConcepts();

  UUID getId();

  @JsonSerialize(using = InstanceQuestionSerializer.class)
  List<InstanceQuestion> getInstanceQuestions();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getMissingValues();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getPartOfGroup();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getQualityStatement();

  @JsonSerialize(using = QuantitySerializer.class)
  @JsonProperty("quantity.prefLabel")
  Optional<Quantity> getQuantity();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  Optional<LocalDate> getReferencePeriodEnd();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  Optional<LocalDate> getReferencePeriodStart();

  @JsonProperty("source.dataset.prefLabel")
  @JsonSerialize(using = SourceDatasetSerializer.class)
  Optional<Dataset> getSource();

  @JsonProperty("source.description")
  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getSourceDescription();

  @JsonProperty("unitType.prefLabel")
  @JsonSerialize(using = UnitTypeSerializer.class)
  Optional<UnitType> getUnitType();

  Optional<String> getValueDomainType();

  Optional<BigDecimal> getValueRangeMax();

  Optional<BigDecimal> getValueRangeMin();

  @JsonProperty("variable.prefLabel")
  @JsonSerialize (using = VariableSerializer.class)
  Optional<Variable> getVariable();

  @JsonProperty(value = "published")
  @JsonSerialize(using = OptionalToStringSerializer.class)
  Optional<Boolean> isPublished();

  @JsonSerialize(using = LangValueSerializer.class)
  Map<String,String> getDataFormat();

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  Optional<Date> getLastModifiedDate();

  @JsonIgnore
  Optional<UserProfile> getLastModifiedByUser();

}
