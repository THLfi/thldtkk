package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.Study;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Arrays.asList;

public class StudyBuilder {

  private UUID id;
  private Map<String, String> prefLabel;
  private List<Study> predecessors = new ArrayList<>();
  private List<Dataset> datasets = new ArrayList<>();

  public StudyBuilder() {
    String defaultName = Study.class.getSimpleName();
    withPrefLabel(defaultName);
  }

  public StudyBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public StudyBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public StudyBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public StudyBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public StudyBuilder withPredecessors(Study... predecessors) {
    this.predecessors.addAll(asList(predecessors));
    return this;
  }

  public StudyBuilder withDatasets(Dataset... datasets) {
    this.datasets = asList(datasets);
    return this;
  }

  public Study build() {
    return new Study(
      id,
      prefLabel,
      datasets,
      predecessors
    );
  }

}
