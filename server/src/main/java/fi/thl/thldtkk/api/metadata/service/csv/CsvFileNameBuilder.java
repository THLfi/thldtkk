package fi.thl.thldtkk.api.metadata.service.csv;

import fi.thl.thldtkk.api.metadata.domain.Dataset;

import java.time.LocalDate;


public class CsvFileNameBuilder {

  private static final String CSV_EXPORT_FILE_NAME_PREFIX = "Variables ";

  public static String getInstanceVariableExportFileName(Dataset dataset, String language) {
    StringBuilder fileName = new StringBuilder();
    fileName.append(CSV_EXPORT_FILE_NAME_PREFIX);

    if (dataset.getPrefLabel().containsKey(language)) {
      String prefLabel = dataset.getPrefLabel().get(language);
      String formattedPrefLabel = prefLabel.replaceAll("\\W", "_");
      formattedPrefLabel = formattedPrefLabel.replaceAll("__+", "_");

      fileName.append(formattedPrefLabel);
      fileName.append(" ");
    }

    fileName.append(LocalDate.now().toString());

    return fileName.toString();
  }

}
