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
import com.google.gson.annotations.SerializedName;
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
  @SerializedName("abstract")
  private Map<String, String> abstract_ = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Map<String, String> registryPolicy = new LinkedHashMap<>();
  private Map<String, String> researchProjectURL = new LinkedHashMap<>();
  private Map<String, String> usageConditionAdditionalInformation = new LinkedHashMap<>();
  private Boolean published;
  private Date referencePeriodStart;
  private Date referencePeriodEnd;
  private Organization owner;
  private List<OrganizationUnit> ownerOrganizationUnit = new ArrayList<>();
  private UsageCondition usageCondition;
  private LifecyclePhase lifecyclePhase;
  private Population population;
  private List<InstanceVariable> instanceVariables = new ArrayList<>();

  public Dataset(UUID id) {
    this.id = requireNonNull(id);
  }

  /**
   * Create a copy of a dataset with different instance variables
   */
  public Dataset(Dataset dataset, List<InstanceVariable> instanceVariables) {
    this(dataset.id);
    this.prefLabel = dataset.prefLabel;
    this.altLabel = dataset.altLabel;
    this.abbreviation = dataset.abbreviation;
    this.abstract_ = dataset.abstract_;
    this.description = dataset.description;
    this.registryPolicy = dataset.registryPolicy;
    this.researchProjectURL = dataset.researchProjectURL;
    this.usageConditionAdditionalInformation = dataset.usageConditionAdditionalInformation;
    this.published = dataset.published;
    this.referencePeriodStart = dataset.referencePeriodStart;
    this.referencePeriodEnd = dataset.referencePeriodEnd;
    this.owner = dataset.owner;
    this.ownerOrganizationUnit = dataset.ownerOrganizationUnit;
    this.usageCondition = dataset.usageCondition;
    this.lifecyclePhase = dataset.lifecyclePhase;
    this.population = dataset.population;
    this.instanceVariables = instanceVariables;
  }

  /**
   * Create a dataset from a node
   */
  public Dataset(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "DataSet"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.altLabel = toLangValueMap(node.getProperties("altLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.abstract_ = toLangValueMap(node.getProperties("abstract"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.registryPolicy = toLangValueMap(node.getProperties("registryPolicy"));
    this.researchProjectURL = toLangValueMap(node.getProperties("researchProjectURL"));
    this.usageConditionAdditionalInformation = toLangValueMap(
        node.getProperties("usageConditionAdditionalInformation"));
    this.published = toBoolean(node.getProperties("published"), false);
    this.referencePeriodStart = toDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toDate(node.getProperties("referencePeriodEnd"), null);
    node.getReferencesFirst("owner").ifPresent(v -> this.owner = new Organization(v));
    node.getReferences("ownerOrganizationUnit")
        .forEach(v -> this.ownerOrganizationUnit.add(new OrganizationUnit(v)));
    node.getReferencesFirst("population").ifPresent(v -> this.population = new Population(v));
    node.getReferencesFirst("usageCondition")
        .ifPresent(v -> this.usageCondition = new UsageCondition(v));
    node.getReferencesFirst("lifecyclePhase")
        .ifPresent(v -> this.lifecyclePhase = new LifecyclePhase(v));
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

  public Map<String, String> getAbstract() {
    return abstract_;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public Map<String, String> getRegistryPolicy() {
    return registryPolicy;
  }

  public Map<String, String> getResearchProjectURL() {
    return researchProjectURL;
  }

  public Map<String, String> getUsageConditionAdditionalInformation() {
    return usageConditionAdditionalInformation;
  }

  public Optional<Boolean> isPublished() {
    return Optional.ofNullable(published);
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

  public List<OrganizationUnit> getOwnerOrganizationUnit() {
    return ownerOrganizationUnit;
  }

  public Optional<Population> getPopulation() {
    return Optional.ofNullable(population);
  }

  public Optional<UsageCondition> getUsageCondition() {
    return Optional.ofNullable(usageCondition);
  }

  public Optional<LifecyclePhase> getLifecyclePhase() {
    return Optional.ofNullable(lifecyclePhase);
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
    props.putAll("abstract", toPropertyValues(abstract_));
    props.putAll("description", toPropertyValues(description));
    props.putAll("registryPolicy", toPropertyValues(registryPolicy));
    props.putAll("researchProjectURL", toPropertyValues(researchProjectURL));
    props.putAll("usageConditionAdditionalInformation",
        toPropertyValues(usageConditionAdditionalInformation));
    isPublished().ifPresent(v -> props.put("published", toPropertyValue(v)));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOwner().ifPresent(v -> refs.put("owner", v.toNode()));
    getOwnerOrganizationUnit().forEach(v -> refs.put("ownerOrganizationUnit", v.toNode()));
    getPopulation().ifPresent(v -> refs.put("population", v.toNode()));
    getUsageCondition().ifPresent(v -> refs.put("usageCondition", v.toNode()));
    getLifecyclePhase().ifPresent(v -> refs.put("lifecyclePhase", v.toNode()));
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
        Objects.equals(abstract_, dataset.abstract_) &&
        Objects.equals(description, dataset.description) &&
        Objects.equals(registryPolicy, dataset.registryPolicy) &&
        Objects.equals(researchProjectURL, dataset.researchProjectURL) &&
        Objects.equals(usageConditionAdditionalInformation,
            dataset.usageConditionAdditionalInformation) &&
        Objects.equals(published, dataset.published) &&
        Objects.equals(referencePeriodStart, dataset.referencePeriodStart) &&
        Objects.equals(referencePeriodEnd, dataset.referencePeriodEnd) &&
        Objects.equals(owner, dataset.owner) &&
        Objects.equals(ownerOrganizationUnit, dataset.ownerOrganizationUnit) &&
        Objects.equals(usageCondition, dataset.usageCondition) &&
        Objects.equals(lifecyclePhase, dataset.lifecyclePhase) &&
        Objects.equals(population, dataset.population) &&
        Objects.equals(instanceVariables, dataset.instanceVariables);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(id, prefLabel, altLabel, abbreviation, abstract_, description, registryPolicy,
            researchProjectURL, usageConditionAdditionalInformation, published,
            referencePeriodStart, referencePeriodEnd, owner, ownerOrganizationUnit, usageCondition,
            lifecyclePhase, population, instanceVariables);
  }

}
