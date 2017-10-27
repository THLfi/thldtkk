
package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import java.util.Arrays;
import java.util.List;

public enum AdditionalCsvField {
  DATASET_ID("dataset.prefLabel", "dataset.id"),
  DATASET_REFERENCE_PERIOD("referencePeriodEnd", "dataset.referencePeriodStart", "dataset.referencePeriodEnd"),
  QUANTITY_ID("quantity.prefLabel", "quantity.id"),
  CODELIST_PROPS("codeList.prefLabel", "codeList.id", "codeList.description", "codeList.owner", "codeList.referenceId", "codeList.codeListType","codeList.codeItems"),
  UNIT_TYPE_PROPS("unitType.prefLabel", "unitType.id", "unitType.description"),
  UNIT_PROPS("unit.prefLabel", "unit.id", "unit.symbol"),
  VARIABLE_PROPS("variable.prefLabel", "variable.id", "variable.description"),
  SOURCE_DATASET_ID("source.dataset.prefLabel", "source.dataset.id");
  
  private String sourceField;
  private List<String> additionalFields; 
  
  AdditionalCsvField(String sourceField, String... additionalFields) {
    this.sourceField = sourceField;
    this.additionalFields = Arrays.asList(additionalFields);
  }
  
  public String getSourceField() {
    return this.sourceField;
  }
  
  public List<String> getAdditionalFields() {
    return this.additionalFields;
  }
   
}