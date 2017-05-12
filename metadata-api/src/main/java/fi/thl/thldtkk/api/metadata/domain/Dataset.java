package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import java.util.ArrayList;
import java.util.Date;
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
  private Map<String, String> registryPolicy = new LinkedHashMap<>();
  private Boolean isPublic;
  private Date referencePeriodStart;
  private Date referencePeriodEnd;
  private Organization owner;
  private Population population;
  private List<InstanceVariable> instanceVariables = new ArrayList<>();

  public Dataset(UUID id) {
    this.id = requireNonNull(id);
  }

  public Dataset(UUID id,
    Map<String, String> prefLabel,
    Map<String, String> altLabel,
    Map<String, String> abbreviation,
    Map<String, String> shortDescription,
    Map<String, String> description,
    Map<String, String> registryPolicy,
    boolean isPublic,
    Date referencePeriodStart,
    Date referencePeriodEnd,
    Organization owner,
    Population population,
    List<InstanceVariable> instanceVariables) {
    this(id);
    this.prefLabel = prefLabel;
    this.altLabel = altLabel;
    this.abbreviation = abbreviation;
    this.shortDescription = shortDescription;
    this.description = description;
    this.registryPolicy = registryPolicy;
    this.isPublic = isPublic;
    this.referencePeriodStart = referencePeriodStart;
    this.referencePeriodEnd = referencePeriodEnd;
    this.owner = owner;
    this.population = population;
    this.instanceVariables = instanceVariables;
  }

  public Dataset(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "DataSet"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.altLabel = toLangValueMap(node.getProperties("altLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.shortDescription = toLangValueMap(node.getProperties("shortDescription"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.registryPolicy = toLangValueMap(node.getProperties("registryPolicy"));
    this.isPublic = toBoolean(node.getProperties("isPublic"), false);
    this.referencePeriodStart = toDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toDate(node.getProperties("referencePeriodEnd"), null);
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

  public Map<String, String> getRegistryPolicy() {
    return registryPolicy;
  }

  public Optional<Boolean> isPublic() {
    return Optional.ofNullable(isPublic);
  }

  public Optional<Date> getReferencePeriodStart() {
    return Optional.ofNullable(referencePeriodStart);
  }

  public Optional<Date> getReferencePeriodEnd() {
    return Optional.ofNullable(referencePeriodEnd);
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

  /**
   * Transforms dataset into node
   */
  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("altLabel", toPropertyValues(altLabel));
    props.putAll("abbreviation", toPropertyValues(abbreviation));
    props.putAll("shortDescription", toPropertyValues(shortDescription));
    props.putAll("description", toPropertyValues(description));
    props.putAll("registryPolicy", toPropertyValues(registryPolicy));
    isPublic().ifPresent(v -> props.put("isPublic", toPropertyValue(v)));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOwner().ifPresent(v -> refs.put("owner", v.toNode()));
    getPopulation().ifPresent(v -> refs.put("population", v.toNode()));
    getInstanceVariables().forEach(v -> refs.put("instanceVariable", v.toNode()));

    return new Node(id, "DataSet", props, refs);
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
      Objects.equals(registryPolicy, dataset.registryPolicy) &&
      Objects.equals(isPublic, dataset.isPublic) &&
      Objects.equals(referencePeriodStart, dataset.referencePeriodStart) &&
      Objects.equals(referencePeriodEnd, dataset.referencePeriodEnd) &&
      Objects.equals(owner, dataset.owner) &&
      Objects.equals(population, dataset.population) &&
      Objects.equals(instanceVariables, dataset.instanceVariables);
  }

  @Override
  public int hashCode() {
    return Objects
      .hash(id, prefLabel, altLabel, abbreviation, shortDescription, description, registryPolicy,
        isPublic, referencePeriodStart, referencePeriodEnd, owner, population, instanceVariables);
  }

}
