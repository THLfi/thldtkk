package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Dataset {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> altLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private Map<String, String> shortDescription = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Organization owner;
  private Population population;
  private List<InstanceVariable> instanceVariables = new ArrayList<>();

  public Dataset(UUID id) {
    this.id = requireNonNull(id);
  }

  public Dataset(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "DataSet"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.altLabel = toLangValueMap(node.getProperties("altLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.shortDescription = toLangValueMap(node.getProperties("shortDescription"));
    this.description = toLangValueMap(node.getProperties("description"));
    node.getReferencesFirst("owner").ifPresent(v -> this.owner = new Organization(v));
    node.getReferencesFirst("population").ifPresent(v -> this.population = new Population(v));
    node.getReferences("instanceVariable")
        .forEach(v -> this.instanceVariables.add(new InstanceVariable(v)));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getAltLabel() {
    return altLabel;
  }

  public Map<String, String> getAbbreviation() {
    return abbreviation;
  }

  public Map<String, String> getShortDescription() {
    return shortDescription;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public Optional<Organization> getOwner() {
    return Optional.ofNullable(owner);
  }

  public Optional<Population> getPopulation() {
    return Optional.ofNullable(population);
  }

  public List<InstanceVariable> getInstanceVariables() {
    return instanceVariables;
  }

  public Node toNode() {
    Node node = new Node(id, "DataSet");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("altLabel", toPropertyValues(altLabel));
    node.addProperties("abbreviation", toPropertyValues(abbreviation));
    node.addProperties("shortDescription", toPropertyValues(shortDescription));
    node.addProperties("description", toPropertyValues(description));
    getOwner().ifPresent(v -> node.addReference("owner", v.toNode()));
    getPopulation().ifPresent(v -> node.addReference("population", v.toNode()));
    getInstanceVariables().forEach(v -> node.addReference("instanceVariable", v.toNode()));
    return node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dataset dataset = (Dataset) o;
    return Objects.equals(id, dataset.id) &&
        Objects.equals(prefLabel, dataset.prefLabel) &&
        Objects.equals(altLabel, dataset.altLabel) &&
        Objects.equals(abbreviation, dataset.abbreviation) &&
        Objects.equals(shortDescription, dataset.shortDescription) &&
        Objects.equals(description, dataset.description) &&
        Objects.equals(owner, dataset.owner) &&
        Objects.equals(population, dataset.population) &&
        Objects.equals(instanceVariables, dataset.instanceVariables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, altLabel, abbreviation, shortDescription,
        description, owner, population, instanceVariables);
  }

}
