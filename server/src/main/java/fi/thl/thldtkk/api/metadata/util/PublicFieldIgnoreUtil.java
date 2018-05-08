package fi.thl.thldtkk.api.metadata.util;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;

import java.util.List;

public class PublicFieldIgnoreUtil {

  private PublicFieldIgnoreUtil() {
  }

  public static List<Study> sanitizeStudyList(List<Study> studyList) {
    studyList.forEach(PublicFieldIgnoreUtil::sanitizeStudy);
    return studyList;
  }

  public static List<Dataset> sanitizeDatasetList(List<Dataset> datasetList) {
    datasetList.forEach(PublicFieldIgnoreUtil::sanitizeDataset);
    return datasetList;
  }

  public static List<InstanceVariable> sanitizeInstanceVariableList(List<InstanceVariable> instanceVariableList) {
    instanceVariableList.forEach(PublicFieldIgnoreUtil::sanitizeInstanceVariable);
    return instanceVariableList;
  }

  public static Study sanitizeStudy(Study study) {
    study.setPublished(null);
    study.setComment(null);

    // sanitize linked entities
    sanitizeStudyList(study.getPredecessors());
    sanitizeStudyList(study.getSuccessors());
    sanitizeDatasetList(study.getDatasets());

    return study;
  }

  public static Dataset sanitizeDataset(Dataset dataset) {
    dataset.setPublished(null);
    dataset.setComment(null);

    // sanitize linked entities
    if (dataset.getStudy().isPresent()) {
      sanitizeStudy(dataset.getStudy().get());
    }
    sanitizeDatasetList(dataset.getPredecessors());
    sanitizeDatasetList(dataset.getSuccessors());
    sanitizeInstanceVariableList(dataset.getInstanceVariables());

    return dataset;
  }

  public static InstanceVariable sanitizeInstanceVariable(InstanceVariable instanceVariable) {
    instanceVariable.setPublished(null);

    // sanitize linked entities
    if (instanceVariable.getSource().isPresent()) {
      sanitizeDataset(instanceVariable.getSource().get());
    }
    if (instanceVariable.getDataset().isPresent()) {
      sanitizeDataset(instanceVariable.getDataset().get());
    }

    return instanceVariable;
  }
}
