package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Population;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class DatasetBuilder {
  private UUID id;
  private Map<String, String> prefLabel;
  private Population population;
  private List<InstanceVariable> instanceVariables;

  public DatasetBuilder() {
    String defaultName = Dataset.class.getSimpleName();
    withPrefLabel(defaultName);
    withInstanceVariables();
  }

  public DatasetBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public DatasetBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public DatasetBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public DatasetBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }


  public DatasetBuilder withPopulation(Population population) {
    this.population = population;
    return this;
  }

  public DatasetBuilder withInstanceVariables(InstanceVariable... instanceVariables) {
    this.instanceVariables = Arrays.asList(instanceVariables);
    return this;
  }

  public Dataset build() {
    return new Dataset(
      id,
      prefLabel,
      population,
      instanceVariables
    );
  }
}
