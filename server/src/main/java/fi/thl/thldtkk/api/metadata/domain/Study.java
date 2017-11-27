package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class Study implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "Study";

  private UUID id;
  private Date lastModifiedDate;
  private UserProfile lastModifiedByUser;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> altLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Map<String, String> registryPolicy = new LinkedHashMap<>();
  private Map<String, String> usageConditionAdditionalInformation = new LinkedHashMap<>();
  private LocalDate referencePeriodStart;
  private LocalDate referencePeriodEnd;
  private LocalDate collectionStartDate;
  private LocalDate collectionEndDate;
  private String numberOfObservationUnits;
  private Map<String, String> freeConcepts = new LinkedHashMap<>();
  private String comment;
  private Boolean published;
  private Organization ownerOrganization;
  private OrganizationUnit ownerOrganizationUnit;
  private UsageCondition usageCondition;
  private LifecyclePhase lifecyclePhase;
  private Population population;
  private Universe universe;
  private List<Link> links = new ArrayList<>();
  private List<Concept> conceptsFromScheme = new ArrayList<>();
  private List<DatasetType> datasetTypes = new ArrayList<>();
  private UnitType unitType;
  private StudyGroup studyGroup;
  @Valid
  private List<PersonInRole> personInRoles = new ArrayList<>();
  private List<Dataset> datasets = new ArrayList<>();
  private List<Study> predecessors = new ArrayList<>();
  private List<Study> successors = new ArrayList<>();

  /**
   * Required by GSON deserialization.
   */
  private Study() {

  }

  public Study(UUID id) {
    this.id = requireNonNull(id);
  }

  public Study(Node node) {
    this(node.getId());

    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));

    this.lastModifiedDate = node.getLastModifiedDate();

    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.altLabel = toLangValueMap(node.getProperties("altLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.registryPolicy = toLangValueMap(node.getProperties("registryPolicy"));
    this.usageConditionAdditionalInformation = toLangValueMap(node.getProperties("usageConditionAdditionalInformation"));
    this.referencePeriodStart = toLocalDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toLocalDate(node.getProperties("referencePeriodEnd"), null);
    this.collectionStartDate = toLocalDate(node.getProperties("collectionStartDate"), null);
    this.collectionEndDate = toLocalDate(node.getProperties("collectionEndDate"), null);
    this.numberOfObservationUnits = PropertyMappings.toString(node.getProperties("numberOfObservationUnits"));
    this.freeConcepts = toLangValueMap(node.getProperties("freeConcepts"));
    this.comment = PropertyMappings.toString(node.getProperties("comment"));
    this.published = toBoolean(node.getProperties("published"), false);

    node.getReferencesFirst("lastModifiedByUser")
      .ifPresent(v -> this.lastModifiedByUser = new UserInformation(new UserProfile(v)));
    node.getReferencesFirst("ownerOrganization")
      .ifPresent(oo -> this.ownerOrganization = new Organization(oo));
    node.getReferencesFirst("ownerOrganizationUnit")
      .ifPresent(oou -> this.ownerOrganizationUnit = new OrganizationUnit(oou));
    node.getReferencesFirst("population")
      .ifPresent(p -> this.population = new Population(p));
    node.getReferencesFirst("universe")
      .ifPresent(u -> this.universe = new Universe(u));
    node.getReferencesFirst("usageCondition")
      .ifPresent(uc -> this.usageCondition = new UsageCondition(uc));
    node.getReferencesFirst("lifecyclePhase")
      .ifPresent(lp -> this.lifecyclePhase = new LifecyclePhase(lp));
    node.getReferences("links")
      .forEach(l -> this.links.add(new Link(l)));
    node.getReferences("datasetTypes")
      .forEach(dt -> this.datasetTypes.add(new DatasetType(dt)));
    node.getReferencesFirst("unitType")
      .ifPresent(ut -> this.unitType = new UnitType(ut));
    node.getReferencesFirst("studyGroup")
      .ifPresent(sg -> this.studyGroup = new StudyGroup(sg));
    node.getReferences("personInRoles")
      .forEach(pir -> this.personInRoles.add(new PersonInRole(pir)));
    node.getReferences("conceptsFromScheme")
      .forEach(c -> this.conceptsFromScheme.add(new Concept(c)));
    node.getReferences("dataSets")
      .forEach(d -> this.datasets.add(new Dataset(d)));
    node.getReferences("predecessors")
      .forEach(s -> this.predecessors.add(new Study(s)));

    node.getReferrers("predecessors")
      .forEach(s -> this.successors.add(new Study(s)));
  }

  /**
   * Constructor for testing purposes. Add attributes if needed.
   */
  public Study(UUID id,
               Map<String, String> prefLabel,
               List<Dataset> datasets,
               List<Study> predecessors) {
    this.id = id;
    this.prefLabel = prefLabel;
    this.datasets = datasets;
    this.predecessors = predecessors;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  // TODO: Add date format? Currently serialized date looks like this: "lastModifiedDate": "Nov 1, 2017 4:26:32 PM"
  // See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date
  // And http://www.ecma-international.org/ecma-262/5.1/#sec-15.9.1.15
  public Optional<Date> getLastModifiedDate() {
    return Optional.ofNullable(lastModifiedDate);
  }

  public Optional<UserProfile> getLastModifiedByUser() {
    return Optional.ofNullable(lastModifiedByUser);
  }

  public void setLastModifiedByUser(UserProfile userProfile) {
    this.lastModifiedByUser = userProfile;
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

  public Map<String, String> getUsageConditionAdditionalInformation() {
    return usageConditionAdditionalInformation;
  }

  public Optional<LocalDate> getReferencePeriodStart() {
    return Optional.ofNullable(referencePeriodStart);
  }

  public Optional<LocalDate> getReferencePeriodEnd() {
    return Optional.ofNullable(referencePeriodEnd);
  }

  public Optional<LocalDate> getCollectionStartDate() {
    return Optional.ofNullable(collectionStartDate);
  }

  public Optional<LocalDate> getCollectionEndDate() {
    return Optional.ofNullable(collectionEndDate);
  }

  public Optional<String> getNumberOfObservationUnits() {
    return Optional.ofNullable(numberOfObservationUnits);
  }

  public Map<String, String> getFreeConcepts() {
    return freeConcepts;
  }

  public Optional<String> getComment() {
    return Optional.ofNullable(comment);
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Optional<Boolean> isPublished() {
    return Optional.ofNullable(published);
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public Optional<Organization> getOwnerOrganization() {
    return Optional.ofNullable(ownerOrganization);
  }

  public Optional<OrganizationUnit> getOwnerOrganizationUnit() {
    return Optional.ofNullable(ownerOrganizationUnit);
  }

  public Optional<Population> getPopulation() {
    return Optional.ofNullable(population);
  }

  public Optional<Universe> getUniverse() {
    return Optional.ofNullable(universe);
  }

  public Optional<UsageCondition> getUsageCondition() {
    return Optional.ofNullable(usageCondition);
  }

  public Optional<LifecyclePhase> getLifecyclePhase() {
    return Optional.ofNullable(lifecyclePhase);
  }

  public List<Link> getLinks() {
    return links;
  }

  public List<DatasetType> getDatasetTypes() {
    return datasetTypes;
  }

  public Optional<UnitType> getUnitType() {
    return Optional.ofNullable(unitType);
  }

  public Optional<StudyGroup> getStudyGroup() {
    return Optional.ofNullable(studyGroup);
  }

  public List<PersonInRole> getPersonInRoles() {
    return personInRoles;
  }

  public List<Concept> getConceptsFromScheme() {
    return conceptsFromScheme;
  }

  public List<Dataset> getDatasets() {
    return datasets;
  }

  public void setDatasets(List<Dataset> datasets) {
    this.datasets = datasets;
  }

  public List<Study> getPredecessors() {
    return predecessors;
  }

  public void setPredecessors(List<Study> predecessors) {
    this.predecessors = predecessors;
  }

  public List<Study> getSuccessors() {
    return successors;
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
    props.putAll("usageConditionAdditionalInformation", toPropertyValues(usageConditionAdditionalInformation));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));
    getCollectionStartDate().ifPresent(v -> props.put("collectionStartDate", toPropertyValue(v)));
    getCollectionEndDate().ifPresent(v -> props.put("collectionEndDate", toPropertyValue(v)));
    getNumberOfObservationUnits().ifPresent(v -> props.put("numberOfObservationUnits", toPropertyValue(v)));
    props.putAll("freeConcepts", toPropertyValues(freeConcepts));
    getComment().ifPresent(v -> props.put("comment", toPropertyValue(v)));
    isPublished().ifPresent(v -> props.put("published", toPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getLastModifiedByUser().ifPresent(v -> refs.put("lastModifiedByUser", v.toNode()));
    getOwnerOrganization().ifPresent(oo -> refs.put("ownerOrganization", oo.toNode()));
    getOwnerOrganizationUnit().ifPresent(oou -> refs.put("ownerOrganizationUnit", oou.toNode()));
    getPopulation().ifPresent(p -> refs.put("population", p.toNode()));
    getUniverse().ifPresent(u -> refs.put("universe", u.toNode()));
    getUsageCondition().ifPresent(uc -> refs.put("usageCondition", uc.toNode()));
    getLifecyclePhase().ifPresent(lp -> refs.put("lifecyclePhase", lp.toNode()));
    getDatasetTypes().forEach(dt -> refs.put("datasetTypes", dt.toNode()));
    getLinks().forEach(l -> refs.put("links", l.toNode()));
    getConceptsFromScheme().forEach(c -> refs.put("conceptsFromScheme", c.toNode()));
    getUnitType().ifPresent(ut -> refs.put("unitType", ut.toNode()));
    getStudyGroup().ifPresent(sg -> refs.put("studyGroup", sg.toNode()));
    getPersonInRoles().forEach(pir -> refs.put("personInRoles", pir.toNode()));
    getDatasets().forEach(d -> refs.put("dataSets", d.toNode()));
    getPredecessors().forEach(d -> refs.put("predecessors", d.toNode()));

    return new Node(id, TERMED_NODE_CLASS, props, refs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Study study = (Study) o;
    return Objects.equals(id, study.id)
            && Objects.equals(lastModifiedDate, study.lastModifiedDate)
            && Objects.equals(lastModifiedByUser, study.lastModifiedByUser)
            && Objects.equals(prefLabel, study.prefLabel)
            && Objects.equals(altLabel, study.altLabel)
            && Objects.equals(abbreviation, study.abbreviation)
            && Objects.equals(description, study.description)
            && Objects.equals(registryPolicy, study.registryPolicy)
            && Objects.equals(usageConditionAdditionalInformation, study.usageConditionAdditionalInformation)
            && Objects.equals(published, study.published)
            && Objects.equals(referencePeriodStart, study.referencePeriodStart)
            && Objects.equals(referencePeriodEnd, study.referencePeriodEnd)
            && Objects.equals(collectionStartDate, study.collectionStartDate)
            && Objects.equals(collectionEndDate, study.collectionEndDate)
            && Objects.equals(ownerOrganization, study.ownerOrganization)
            && Objects.equals(ownerOrganizationUnit, study.ownerOrganizationUnit)
            && Objects.equals(usageCondition, study.usageCondition)
            && Objects.equals(lifecyclePhase, study.lifecyclePhase)
            && Objects.equals(population, study.population)
            && Objects.equals(universe, study.universe)
            && Objects.equals(numberOfObservationUnits, numberOfObservationUnits)
            && Objects.equals(comment, study.comment)
            && Objects.equals(links, study.links)
            && Objects.equals(conceptsFromScheme, study.conceptsFromScheme)
            && Objects.equals(freeConcepts, study.freeConcepts)
            && Objects.equals(datasetTypes, study.datasetTypes)
            && Objects.equals(unitType, study.unitType)
            && Objects.equals(studyGroup, study.studyGroup)
            && Objects.equals(personInRoles, study.personInRoles)
            && Objects.equals(datasets, study.datasets)
            && Objects.equals(predecessors, study.predecessors);
  }

  @Override
  public int hashCode() {
      return Objects.hash(
        id,
        lastModifiedDate,
        lastModifiedByUser,
        prefLabel,
        altLabel,
        abbreviation,
        description,
        registryPolicy,
        usageConditionAdditionalInformation,
        published,
        referencePeriodStart,
        referencePeriodEnd, ownerOrganization,
        ownerOrganizationUnit,
        usageCondition,
        lifecyclePhase,
        population,
        universe,
        comment,
        datasetTypes,
        numberOfObservationUnits,
        links,
        conceptsFromScheme,
        freeConcepts,
        unitType,
        studyGroup,
        personInRoles,
        collectionStartDate,
        collectionEndDate,
        datasets,
        predecessors);
  }

}
