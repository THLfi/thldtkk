package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.UUID;

public class Dataset {

    private UUID id;

    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> altLabel = new LinkedHashMap<>();
    private Map<String, String> abbreviation = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();
    private Map<String, String> registryPolicy = new LinkedHashMap<>();
    private Map<String, String> researchProjectURL = new LinkedHashMap<>();
    private Map<String, String> usageConditionAdditionalInformation
            = new LinkedHashMap<>();
    private Boolean published;
    private LocalDate referencePeriodStart;
    private LocalDate referencePeriodEnd;
    private Organization owner;
    private List<OrganizationUnit> ownerOrganizationUnit = new ArrayList<>();
    private UsageCondition usageCondition;
    private LifecyclePhase lifecyclePhase;
    private Population population;
    private List<InstanceVariable> instanceVariables = new ArrayList<>();
    private String comment;
    private String numberOfObservationUnits;
    private DatasetType datasetType;

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
        this.description = dataset.description;
        this.registryPolicy = dataset.registryPolicy;
        this.researchProjectURL = dataset.researchProjectURL;
        this.usageConditionAdditionalInformation
                = dataset.usageConditionAdditionalInformation;
        this.published = dataset.published;
        this.referencePeriodStart = dataset.referencePeriodStart;
        this.referencePeriodEnd = dataset.referencePeriodEnd;
        this.owner = dataset.owner;
        this.ownerOrganizationUnit = dataset.ownerOrganizationUnit;
        this.usageCondition = dataset.usageCondition;
        this.lifecyclePhase = dataset.lifecyclePhase;
        this.population = dataset.population;
        this.instanceVariables = instanceVariables;
        this.comment = dataset.comment;
        this.numberOfObservationUnits = dataset.numberOfObservationUnits;
        this.datasetType = dataset.datasetType;
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
        this.description = toLangValueMap(node.getProperties("description"));
        this.registryPolicy = toLangValueMap(node
                .getProperties("registryPolicy"));
        this.researchProjectURL = toLangValueMap(node.getProperties(
                "researchProjectURL"));
        this.usageConditionAdditionalInformation = toLangValueMap(
                node.getProperties("usageConditionAdditionalInformation"));
        this.published = toBoolean(node.getProperties("published"), false);
        this.referencePeriodStart = toLocalDate(node.getProperties(
                "referencePeriodStart"), null);
        this.referencePeriodEnd = toLocalDate(node.getProperties(
                "referencePeriodEnd"), null);
        node.getReferencesFirst("owner").ifPresent(v -> this.owner
                = new Organization(v));
        node.getReferences("ownerOrganizationUnit")
                .forEach(v -> this.ownerOrganizationUnit.add(
                        new OrganizationUnit(v)));
        node.getReferencesFirst("population").ifPresent(v -> this.population
                = new Population(v));
        node.getReferencesFirst("usageCondition")
                .ifPresent(v -> this.usageCondition = new UsageCondition(v));
        node.getReferencesFirst("lifecyclePhase")
                .ifPresent(v -> this.lifecyclePhase = new LifecyclePhase(v));
        node.getReferences("instanceVariable")
                .forEach(v -> this.instanceVariables
                        .add(new InstanceVariable(v)));
        node.getReferencesFirst("datasetType")
                .ifPresent(v -> this.datasetType = new DatasetType(v));
 


        this.comment = PropertyMappings.toString(node.getProperties("comment"));
        this.numberOfObservationUnits = PropertyMappings.toString(node.getProperties("numberOfObservationUnits"));
    }

    public UUID getId() {
        return id;
    }

    public Optional<String> getNumberOfObservationUnits() {
        return Optional.ofNullable(numberOfObservationUnits);
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

    public Optional<LocalDate> getReferencePeriodStart() {
        return Optional.ofNullable(referencePeriodStart);
    }

    public Optional<LocalDate> getReferencePeriodEnd() {
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

    public Optional<DatasetType> getDatasetType() {
        return Optional.ofNullable(datasetType);
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    /**
     * Transforms dataset into node
     */
    public Node toNode() {
        Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
        props.putAll("prefLabel", toPropertyValues(prefLabel));
        props.putAll("altLabel", toPropertyValues(altLabel));
        props.putAll("abbreviation", toPropertyValues(abbreviation));
        props.putAll("description", toPropertyValues(description));
        props.putAll("registryPolicy", toPropertyValues(registryPolicy));
        props.putAll("researchProjectURL", toPropertyValues(researchProjectURL));
        props.putAll("usageConditionAdditionalInformation",
                toPropertyValues(usageConditionAdditionalInformation));
        isPublished().ifPresent(v -> props.put("published", toPropertyValue(v)));
        getReferencePeriodStart().ifPresent(v -> props.put(
                "referencePeriodStart", toPropertyValue(v)));
        getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd",
                toPropertyValue(v)));

        Multimap<String, Node> refs = LinkedHashMultimap.create();
        getOwner().ifPresent(v -> refs.put("owner", v.toNode()));
        getOwnerOrganizationUnit().forEach(v -> refs
                .put("ownerOrganizationUnit", v.toNode()));
        getPopulation().ifPresent(v -> refs.put("population", v.toNode()));
        getUsageCondition().ifPresent(v -> refs
                .put("usageCondition", v.toNode()));
        getLifecyclePhase().ifPresent(v -> refs
                .put("lifecyclePhase", v.toNode()));
        getDatasetType().ifPresent(v -> refs
                .put("datasetType", v.toNode()));
        getInstanceVariables().forEach(v -> refs.put("instanceVariable", v
                .toNode()));
        getComment().ifPresent((v -> props.put("comment", toPropertyValue(v))));
        getNumberOfObservationUnits().ifPresent((v -> props.put("numberOfObservationUnits", toPropertyValue(v))));
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
        return Objects.equals(id, dataset.id)
                && Objects.equals(prefLabel, dataset.prefLabel)
                && Objects.equals(altLabel, dataset.altLabel)
                && Objects.equals(abbreviation, dataset.abbreviation)
                && Objects.equals(description, dataset.description)
                && Objects.equals(registryPolicy, dataset.registryPolicy)
                && Objects
                .equals(researchProjectURL, dataset.researchProjectURL)
                && Objects.equals(usageConditionAdditionalInformation,
                        dataset.usageConditionAdditionalInformation)
                && Objects.equals(published, dataset.published)
                && Objects.equals(referencePeriodStart,
                        dataset.referencePeriodStart)
                && Objects
                .equals(referencePeriodEnd, dataset.referencePeriodEnd)
                && Objects.equals(owner, dataset.owner)
                && Objects.equals(ownerOrganizationUnit,
                        dataset.ownerOrganizationUnit)
                && Objects.equals(usageCondition, dataset.usageCondition)
                && Objects.equals(lifecyclePhase, dataset.lifecyclePhase)
                && Objects.equals(population, dataset.population)
                && Objects.equals(instanceVariables, dataset.instanceVariables)
                && Objects.equals(numberOfObservationUnits, numberOfObservationUnits)
                && Objects.equals(datasetType, dataset.datasetType)
                && Objects.equals(comment, dataset.comment);

    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, prefLabel, altLabel, abbreviation, description,
                        registryPolicy,
                        researchProjectURL, usageConditionAdditionalInformation,
                        published,
                        referencePeriodStart, referencePeriodEnd, owner,
                        ownerOrganizationUnit, usageCondition,
                        lifecyclePhase, population, instanceVariables, comment,
                        datasetType, numberOfObservationUnits);

    }

}
