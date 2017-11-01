
package fi.thl.thldtkk.api.metadata.service.csv.serialize;

import java.util.Arrays;
import java.util.List;

public enum AdditionalCsvField {
  DATASET("dataset.prefLabel", "dataset.id"),
  DATASET_REFERENCE_PERIOD("referencePeriodEnd", "dataset.referencePeriodStart", "dataset.referencePeriodEnd"),
  QUANTITY("quantity.prefLabel", "quantity.id"),
  CODELIST("codeList.prefLabel", "codeList.id", "codeList.description", "codeList.owner", "codeList.referenceId", "codeList.codeListType","codeList.codeItems"),
  UNIT_TYPE("unitType.prefLabel", "unitType.id", "unitType.description"),
  UNIT("unit.prefLabel", "unit.id", "unit.symbol"),
  VARIABLE("variable.prefLabel", "variable.id", "variable.description"),
  SOURCE_DATASET("source.dataset.prefLabel", "source.dataset.id");
  
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