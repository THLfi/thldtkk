package fi.thl.thldtkk.api.metadata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.SecurityClassification;
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
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toOptionalString;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valuesToEnumCollection;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valueToEnum;
import static java.util.Objects.requireNonNull;

public class Study implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "Study";

  private UUID id;
  private Date lastModifiedDate;
  private Boolean published;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> altLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private StudyType studyType;
  private Map<String, String> usageConditionAdditionalInformation = new LinkedHashMap<>();
  private LocalDate referencePeriodStart;
  private LocalDate referencePeriodEnd;
  private LocalDate collectionStartDate;
  private LocalDate collectionEndDate;
  private String numberOfObservationUnits;
  private Map<String, String> freeConcepts = new LinkedHashMap<>();
  private Boolean personRegistry;
  private Map<String, String> registryPolicy = new LinkedHashMap<>();
  private Map<String, String> purposeOfPersonRegistry = new LinkedHashMap<>();
  private Map<String, String> usageOfPersonalInformation = new LinkedHashMap<>();
  private Map<String, String> personRegistrySources = new LinkedHashMap<>();
  private Map<String, String> personRegisterDataTransfers = new LinkedHashMap<>();
  private Map<String, String> personRegisterDataTransfersOutsideEuOrEea = new LinkedHashMap<>();
  private List<LegalBasisForHandlingPersonalData> legalBasisForHandlingPersonalData = new ArrayList<>();
  private Map<String, String> otherLegalBasisForHandlingPersonalData = new LinkedHashMap<>();
  private Boolean containsSensitivePersonalData;
  private List<LegalBasisForHandlingSensitivePersonalData> legalBasisForHandlingSensitivePersonalData = new ArrayList<>();
  private Map<String, String> otherLegalBasisForHandlingSensitivePersonalData = new LinkedHashMap<>();
  private List<TypeOfSensitivePersonalData> typeOfSensitivePersonalData = new ArrayList<>();
  private Map<String, String> otherTypeOfSensitivePersonalData = new LinkedHashMap<>();
  private Map<String, String> partiesAndSharingOfResponsibilityInCollaborativeStudy = new LinkedHashMap<>();
  private Map<String, String> studyPerformers = new LinkedHashMap<>();
  private Boolean isScientificStudy;
  private List<GroupOfRegistree> groupsOfRegistrees = new ArrayList<>();
  private Map<String, String> otherGroupsOfRegistrees = new LinkedHashMap<>();
  private List<ReceivingGroup> receivingGroups = new ArrayList<>();
  private Map<String, String> otherReceivingGroups = new LinkedHashMap<>();
  private Boolean profilingAndAutomation;
  private Map<String, String> profilingAndAutomationDescription;
  private Boolean directIdentityInformation;
  private Map<String, String> directIdentityInformationDescription;

  // Data security
  private ConfidentialityClass confidentialityClass;
  private Map<String, String> groundsForConfidentiality = new LinkedHashMap<>();
  private SecurityClassification securityClassification;
  private List<PrincipleForPhysicalSecurity> principlesForPhysicalSecurity = new ArrayList<>();
  private List<PrincipleForDigitalSecurity> principlesForDigitalSecurity = new ArrayList<>();

  private List<SupplementaryPhysicalSecurityPrinciple> otherPrinciplesForPhysicalSecurity = new ArrayList<>();
  private List<SupplementaryDigitalSecurityPrinciple> otherPrinciplesForDigitalSecurity = new ArrayList<>();

  // Archiving
  private LocalDate dataProcessingStartDate;
  private LocalDate dataProcessingEndDate;
  private RetentionPolicy retentionPolicy;
  private Map<String, String> retentionPeriod = new LinkedHashMap<>();
  private Map<String, String> groundsForRetention = new LinkedHashMap<>();
  private Map<String, String> nationalArchivesFinlandArchivalDecision = new LinkedHashMap<>();
  private List<ExistenceForm> existenceForms = new ArrayList<>();
  private Map<String, String> physicalLocation = new LinkedHashMap<>();
  private String comment;
  private String externalId;
  private PostStudyRetentionOfPersonalData postStudyRetentionOfPersonalData;

  private UserProfile lastModifiedByUser;
  private Organization ownerOrganization;
  private OrganizationUnit ownerOrganizationUnit;
  @Valid
  private List<PersonInRole> personInRoles = new ArrayList<>();
  private List<AssociatedOrganization> associatedOrganizations = new ArrayList<>();
  private List<Link> links = new ArrayList<>();
  private UsageCondition usageCondition;
  private Universe universe;
  private Population population;
  private UnitType unitType;
  private LifecyclePhase lifecyclePhase;
  private List<DatasetType> datasetTypes = new ArrayList<>();
  private List<Concept> conceptsFromScheme = new ArrayList<>();
  private StudyGroup studyGroup;
  private List<Dataset> datasets = new ArrayList<>();
  private List<Study> predecessors = new ArrayList<>();
  private List<Study> successors = new ArrayList<>();
  @Valid
  private List<SystemInRole> systemInRoles = new ArrayList<>();
  private Boolean changedAfterPublish;

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
    this.published = toBoolean(node.getProperties("published"), false);
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.altLabel = toLangValueMap(node.getProperties("altLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.studyType = valueToEnum(node.getProperties("studyType"), StudyType.class);
    this.usageConditionAdditionalInformation = toLangValueMap(node.getProperties("usageConditionAdditionalInformation"));
    this.referencePeriodStart = toLocalDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toLocalDate(node.getProperties("referencePeriodEnd"), null);
    this.collectionStartDate = toLocalDate(node.getProperties("collectionStartDate"), null);
    this.collectionEndDate = toLocalDate(node.getProperties("collectionEndDate"), null);
    this.numberOfObservationUnits = PropertyMappings.toString(node.getProperties("numberOfObservationUnits"));
    this.freeConcepts = toLangValueMap(node.getProperties("freeConcepts"));
    this.personRegistry = PropertyMappings.toBoolean(node.getProperties("personRegistry"), null);
    this.registryPolicy = toLangValueMap(node.getProperties("registryPolicy"));
    this.purposeOfPersonRegistry = toLangValueMap(node.getProperties("purposeOfPersonRegistry"));
    this.usageOfPersonalInformation = toLangValueMap(node.getProperties("usageOfPersonalInformation"));
    this.personRegistrySources = toLangValueMap(node.getProperties("personRegistrySources"));
    this.personRegisterDataTransfers = toLangValueMap(node.getProperties("personRegisterDataTransfers"));
    this.personRegisterDataTransfersOutsideEuOrEea = toLangValueMap(node.getProperties("personRegisterDataTransfersOutsideEuOrEea"));
    this.legalBasisForHandlingPersonalData = valuesToEnumCollection(node.getProperties("legalBasisForHandlingPersonalData"), LegalBasisForHandlingPersonalData.class, ArrayList::new);
    this.otherLegalBasisForHandlingPersonalData = toLangValueMap(node.getProperties("otherLegalBasisForHandlingPersonalData"));
    this.containsSensitivePersonalData = PropertyMappings.toBoolean(node.getProperties("containsSensitivePersonalData"), null);
    this.legalBasisForHandlingSensitivePersonalData = valuesToEnumCollection(node.getProperties("legalBasisForHandlingSensitivePersonalData"), LegalBasisForHandlingSensitivePersonalData.class, ArrayList::new);
    this.otherLegalBasisForHandlingSensitivePersonalData = toLangValueMap(node.getProperties("otherLegalBasisForHandlingSensitivePersonalData"));
    this.typeOfSensitivePersonalData = valuesToEnumCollection(node.getProperties("typeOfSensitivePersonalData"), TypeOfSensitivePersonalData.class, ArrayList::new);
    this.otherTypeOfSensitivePersonalData = toLangValueMap(node.getProperties("otherTypeOfSensitivePersonalData"));
    this.partiesAndSharingOfResponsibilityInCollaborativeStudy = toLangValueMap(node.getProperties("partiesAndSharingOfResponsibilityInCollaborativeStudy"));
    this.studyPerformers = toLangValueMap(node.getProperties("studyPerformers"));
    this.isScientificStudy = PropertyMappings.toBoolean(node.getProperties("isScientificStudy"), null);
    this.groupsOfRegistrees = valuesToEnumCollection(node.getProperties("groupsOfRegistrees"), GroupOfRegistree.class, ArrayList::new);
    this.otherGroupsOfRegistrees = toLangValueMap(node.getProperties("otherGroupsOfRegistrees"));
    this.receivingGroups = valuesToEnumCollection(node.getProperties("receivingGroups"), ReceivingGroup.class, ArrayList::new);
    this.otherReceivingGroups = toLangValueMap(node.getProperties("otherReceivingGroups"));
    this.profilingAndAutomation = PropertyMappings.toBoolean(node.getProperties("profilingAndAutomation"), null);
    this.profilingAndAutomationDescription = toLangValueMap(node.getProperties("profilingAndAutomationDescription"));
    this.directIdentityInformation = PropertyMappings.toBoolean(node.getProperties("directIdentityInformation"), null);
    this.directIdentityInformationDescription = toLangValueMap(node.getProperties("directIdentityInformationDescription"));

    // Data security
    toOptionalString(node.getProperties("confidentialityClass"))
      .ifPresent(cc -> this.confidentialityClass = ConfidentialityClass.valueOf(cc));
    this.groundsForConfidentiality = toLangValueMap(node.getProperties("groundsForConfidentiality"));
    toOptionalString(node.getProperties("securityClassification"))
      .ifPresent(sc -> this.securityClassification = SecurityClassification.valueOf(sc));
    this.principlesForPhysicalSecurity = valuesToEnumCollection(node.getProperties("principlesForPhysicalSecurity"),
      PrincipleForPhysicalSecurity.class, ArrayList::new);
    this.principlesForDigitalSecurity = valuesToEnumCollection(node.getProperties("principlesForDigitalSecurity"),
      PrincipleForDigitalSecurity.class, ArrayList::new);

    this.comment = PropertyMappings.toString(node.getProperties("comment"));

    // Archival
    this.dataProcessingStartDate = toLocalDate(node.getProperties("dataProcessingStartDate"), null);
    this.dataProcessingEndDate = toLocalDate(node.getProperties("dataProcessingEndDate"), null);
    toOptionalString(node.getProperties("retentionPolicy"))
      .ifPresent(rp -> this.retentionPolicy = RetentionPolicy.valueOf(rp));
    this.retentionPeriod = toLangValueMap(node.getProperties("retentionPeriod"));
    this.groundsForRetention = toLangValueMap(node.getProperties("groundsForRetention"));
    this.nationalArchivesFinlandArchivalDecision = toLangValueMap(node.getProperties("nationalArchivesFinlandArchivalDecision"));
    this.existenceForms = valuesToEnumCollection(node.getProperties("existenceForms"),
      ExistenceForm.class, ArrayList::new);
    this.physicalLocation = toLangValueMap(node.getProperties("physicalLocation"));
    this.externalId = PropertyMappings.toString(node.getProperties("externalId"));
    toOptionalString(node.getProperties("postStudyRetentionOfPersonalData"))
      .ifPresent(cc -> this.postStudyRetentionOfPersonalData = PostStudyRetentionOfPersonalData.valueOf(cc));

    node.getReferencesFirst("lastModifiedByUser")
      .ifPresent(v -> this.lastModifiedByUser = new UserInformation(new UserProfile(v)));
    node.getReferencesFirst("ownerOrganization")
      .ifPresent(oo -> this.ownerOrganization = new Organization(oo));
    node.getReferencesFirst("ownerOrganizationUnit")
      .ifPresent(oou -> this.ownerOrganizationUnit = new OrganizationUnit(oou));
    node.getReferences("personInRoles")
      .forEach(pir -> this.personInRoles.add(new PersonInRole(pir)));
    node.getReferences("associatedOrganizations")
      .forEach(org -> this.associatedOrganizations.add(new AssociatedOrganization(org)));
    node.getReferences("links")
      .forEach(l -> this.links.add(new Link(l)));
    node.getReferencesFirst("usageCondition")
      .ifPresent(uc -> this.usageCondition = new UsageCondition(uc));
    node.getReferencesFirst("universe")
      .ifPresent(u -> this.universe = new Universe(u));
    node.getReferencesFirst("population")
      .ifPresent(p -> this.population = new Population(p));
    node.getReferencesFirst("unitType")
      .ifPresent(ut -> this.unitType = new UnitType(ut));
    node.getReferencesFirst("lifecyclePhase")
      .ifPresent(lp -> this.lifecyclePhase = new LifecyclePhase(lp));
    node.getReferences("conceptsFromScheme")
      .forEach(c -> this.conceptsFromScheme.add(new Concept(c)));
    node.getReferences("datasetTypes")
      .forEach(dt -> this.datasetTypes.add(new DatasetType(dt)));
    node.getReferencesFirst("studyGroup")
      .ifPresent(sg -> this.studyGroup = new StudyGroup(sg));
    node.getReferences("dataSets")
      .forEach(d -> this.datasets.add(new Dataset(d)));
    node.getReferences("predecessors")
      .forEach(s -> this.predecessors.add(new Study(s)));
    node.getReferences("systemInRoles")
      .forEach(sir -> this.systemInRoles.add(new SystemInRole(sir)));
    node.getReferences("otherPrinciplesForPhysicalSecurity")
      .forEach(ops -> this.otherPrinciplesForPhysicalSecurity.add(new SupplementaryPhysicalSecurityPrinciple(ops)));
    node.getReferences("otherPrinciplesForDigitalSecurity")
      .forEach(ods -> this.otherPrinciplesForDigitalSecurity.add(new SupplementaryDigitalSecurityPrinciple(ods)));

    node.getReferrers("predecessors")
      .forEach(s -> this.successors.add(new Study(s)));
  }

  /**
   * Constructor for testing purposes. Add attributes if needed.
   */
  public Study(UUID id,
               Date lastModifiedDate,
               Map<String, String> prefLabel,
               Map<String, String> description,
               Map<String, String> registryPolicy,
               Map<String, String> purposeOfPersonRegistry,
               Map<String, String> usageOfPersonalInformation,
               Map<String, String> personRegistrySources,
               Map<String, String> personRegisterDataTransfers,
               Map<String, String> personRegisterDataTransfersOutsideEuOrEea,
               List<PrincipleForPhysicalSecurity> principlesForPhysicalSecurity,
               List<PrincipleForDigitalSecurity> principlesForDigitalSecurity,
               Map<String, String> partiesAndSharingOfResponsibilityInCollaborativeStudy,
               ConfidentialityClass confidentialityClass,
               Organization ownerOrganization,
               List<PersonInRole> personInRoles,
               List<Dataset> datasets,
               List<Study> predecessors) {
    this.id = id;
    this.lastModifiedDate = lastModifiedDate;
    this.prefLabel = prefLabel;
    this.description = description;
    this.registryPolicy = registryPolicy;
    this.purposeOfPersonRegistry = purposeOfPersonRegistry;
    this.usageOfPersonalInformation = usageOfPersonalInformation;
    this.personRegistrySources = personRegistrySources;
    this.personRegisterDataTransfers = personRegisterDataTransfers;
    this.personRegisterDataTransfersOutsideEuOrEea = personRegisterDataTransfersOutsideEuOrEea;
    this.principlesForPhysicalSecurity = principlesForPhysicalSecurity;
    this.principlesForDigitalSecurity = principlesForDigitalSecurity;
    this.partiesAndSharingOfResponsibilityInCollaborativeStudy = partiesAndSharingOfResponsibilityInCollaborativeStudy;
    this.confidentialityClass = confidentialityClass;
    this.ownerOrganization = ownerOrganization;
    this.personInRoles = personInRoles;
    this.datasets = datasets;
    this.predecessors = predecessors;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<Date> getLastModifiedDate() {
    return Optional.ofNullable(lastModifiedDate);
  }

  public Optional<Boolean> isPublished() {
    return Optional.ofNullable(published);
  }

  public void setPublished(Boolean published) {
    this.published = published;
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

  public Optional<StudyType> getStudyType() {
    return Optional.ofNullable(studyType);
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

  public Optional<Boolean> getPersonRegistry() {
    return Optional.ofNullable(personRegistry);
  }

  public Map<String, String> getRegistryPolicy() {
    return registryPolicy;
  }

  public void setRegistryPolicy(Map<String, String> registryPolicy) {
    this.registryPolicy = registryPolicy;
  }

  public Map<String, String> getPurposeOfPersonRegistry() {
    return purposeOfPersonRegistry;
  }

  public Map<String, String> getUsageOfPersonalInformation() {
    return usageOfPersonalInformation;
  }

  public void setUsageOfPersonalInformation(Map<String, String> usageOfPersonalInformation) {
    this.usageOfPersonalInformation = usageOfPersonalInformation;
  }

  public void setPurposeOfPersonRegistry(Map<String, String> purposeOfPersonRegistry) {
    this.purposeOfPersonRegistry = purposeOfPersonRegistry;
  }

  public Map<String, String> getPersonRegistrySources() {
    return personRegistrySources;
  }

  public void setPersonRegistrySources(Map<String, String> personRegistrySources) {
    this.personRegistrySources = personRegistrySources;
  }

  public Map<String, String> getPersonRegisterDataTransfers() {
    return personRegisterDataTransfers;
  }

  public void setPersonRegisterDataTransfers(Map<String, String> personRegisterDataTransfers) {
    this.personRegisterDataTransfers = personRegisterDataTransfers;
  }

  public Map<String, String> getPersonRegisterDataTransfersOutsideEuOrEta() {
    return personRegisterDataTransfersOutsideEuOrEea;
  }

  public void setPersonRegisterDataTransfersOutsideEuOrEta(Map<String, String> personRegisterDataTransfersOutsideEuOrEea) {
    this.personRegisterDataTransfersOutsideEuOrEea = personRegisterDataTransfersOutsideEuOrEea;
  }

  public Optional<Boolean> getIsScientificStudy() {
    return Optional.ofNullable(isScientificStudy);
  }

  public List<GroupOfRegistree> getGroupsOfRegistrees() {
    return groupsOfRegistrees;
  }

  public Map<String, String> getOtherGroupsOfRegistrees() {
    return otherGroupsOfRegistrees;
  }

  public List<ReceivingGroup> getReceivingGroups() {
    return receivingGroups;
  }

  public Map<String, String> getOtherReceivingGroups() {
    return otherReceivingGroups;
  }

  public Optional<Boolean> getProfilingAndAutomation() {
    return Optional.ofNullable(profilingAndAutomation);
  }

  public Map<String, String> getProfilingAndAutomationDescription() {
    return profilingAndAutomationDescription;
  }

  public Optional<Boolean> getDirectIdentityInformation() {
    return Optional.ofNullable(directIdentityInformation);
  }

  public Map<String, String> getDirectIdentityInformationDescription() {
    return directIdentityInformationDescription;
  }

  public Optional<ConfidentialityClass> getConfidentialityClass() {
    return Optional.ofNullable(confidentialityClass);
  }

  public void setConfidentialityClass(ConfidentialityClass confidentialityClass) {
    this.confidentialityClass = confidentialityClass;
  }
  public Map<String, String> getGroundsForConfidentiality() {
    return groundsForConfidentiality;
  }

  public void setGroundsForConfidentiality(Map<String, String> groundsForConfidentiality) {
    this.groundsForConfidentiality = groundsForConfidentiality;
  }

  public Optional<SecurityClassification> getSecurityClassification() {
    return Optional.ofNullable(securityClassification);
  }

  public void setSecurityClassification(SecurityClassification securityClassification) {
    this.securityClassification = securityClassification;
  }

  public List<PrincipleForPhysicalSecurity> getPrinciplesForPhysicalSecurity() {
    return principlesForPhysicalSecurity;
  }

  public void setPrinciplesForPhysicalSecurity(List<PrincipleForPhysicalSecurity> principlesForPhysicalSecurity) {
    this.principlesForPhysicalSecurity = principlesForPhysicalSecurity;
  }

  public List<PrincipleForDigitalSecurity> getPrinciplesForDigitalSecurity() {
    return principlesForDigitalSecurity;
  }

  public void setPrinciplesForDigitalSecurity(List<PrincipleForDigitalSecurity> principlesForDigitalSecurity) {
    this.principlesForDigitalSecurity = principlesForDigitalSecurity;
  }

  public List<LegalBasisForHandlingPersonalData> getLegalBasisForHandlingPersonalData() {
    return legalBasisForHandlingPersonalData;
  }

  public void setLegalBasisForHandlingPersonalData(List<LegalBasisForHandlingPersonalData> legalBasisForHandlingPersonalData) {
    this.legalBasisForHandlingPersonalData = legalBasisForHandlingPersonalData;
  }

  public Map<String, String> getOtherLegalBasisForHandlingPersonalData() {
    return otherLegalBasisForHandlingPersonalData;
  }

  public void setOtherLegalBasisForHandlingPersonalData(Map<String, String> otherLegalBasisForHandlingPersonalData) {
    this.otherLegalBasisForHandlingPersonalData = otherLegalBasisForHandlingPersonalData;
  }

  public Optional<Boolean> getContainsSensitivePersonalData() {
    return Optional.ofNullable(containsSensitivePersonalData);
  }

  public List<LegalBasisForHandlingSensitivePersonalData> getLegalBasisForHandlingSensitivePersonalData() {
    return legalBasisForHandlingSensitivePersonalData;
  }

  public void setLegalBasisForHandlingSensitivePersonalData(List<LegalBasisForHandlingSensitivePersonalData> legalBasisForHandlingSensitivePersonalData) {
    this.legalBasisForHandlingSensitivePersonalData = legalBasisForHandlingSensitivePersonalData;
  }

  public Map<String, String> getOtherLegalBasisForHandlingSensitivePersonalData() {
    return otherLegalBasisForHandlingSensitivePersonalData;
  }

  public void setOtherLegalBasisForHandlingSensitivePersonalData(Map<String, String> otherLegalBasisForHandlingSensitivePersonalData) {
    this.otherLegalBasisForHandlingSensitivePersonalData = otherLegalBasisForHandlingSensitivePersonalData;
  }

  public List<TypeOfSensitivePersonalData> getTypeOfSensitivePersonalData() {
    return typeOfSensitivePersonalData;
  }

  public void setTypeOfSensitivePersonalData(List<TypeOfSensitivePersonalData> typeOfSensitivePersonalData) {
    this.typeOfSensitivePersonalData = typeOfSensitivePersonalData;
  }

  public Map<String, String> getOtherTypeOfSensitivePersonalData() {
    return otherTypeOfSensitivePersonalData;
  }

  public void setOtherTypeOfSensitivePersonalData(Map<String, String> otherTypeOfSensitivePersonalData) {
    this.otherTypeOfSensitivePersonalData = otherTypeOfSensitivePersonalData;
  }

  public Map<String, String> getPartiesAndSharingOfResponsibilityInCollaborativeStudy() {
    return partiesAndSharingOfResponsibilityInCollaborativeStudy;
  }

  public void setPartiesAndSharingOfResponsibilityInCollaborativeStudy(Map<String, String> partiesAndSharingOfResponsibilityInCollaborativeStudy) {
    this.partiesAndSharingOfResponsibilityInCollaborativeStudy = partiesAndSharingOfResponsibilityInCollaborativeStudy;
  }

  public Map<String, String> getStudyPerformers() {
    return studyPerformers;
  }

  public Optional<LocalDate> getDataProcessingStartDate() {
    return Optional.ofNullable(dataProcessingStartDate);
  }

  public Optional<LocalDate> getDataProcessingEndDate() {
    return Optional.ofNullable(dataProcessingEndDate);
  }

  public Map<String, String> getRetentionPeriod() {
    return retentionPeriod;
  }

  public Optional<RetentionPolicy> getRetentionPolicy() {
    return Optional.ofNullable(retentionPolicy);
  }

  public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
    this.retentionPolicy = retentionPolicy;
  }

  public void setRetentionPeriod(Map<String, String> retentionPeriod) {
    this.retentionPeriod = retentionPeriod;
  }

  public Map<String, String> getGroundsForRetention() {
    return groundsForRetention;
  }

  public void setGroundsForRetention(Map<String, String> groundsForRetention) {
    this.groundsForRetention = groundsForRetention;
  }

  public Map<String, String> getNationalArchivesFinlandArchivalDecision() {
    return nationalArchivesFinlandArchivalDecision;
  }

  public Optional<PostStudyRetentionOfPersonalData> getPostStudyRetentionOfPersonalData() {
    return Optional.ofNullable(postStudyRetentionOfPersonalData);
  }

  public void setPostStudyRetentionOfPersonalData(PostStudyRetentionOfPersonalData postStudyRetentionOfPersonalData) {
    this.postStudyRetentionOfPersonalData = postStudyRetentionOfPersonalData;
  }

  public Map<String, String> getPhysicalLocation() {
    return physicalLocation;
  }

  public List<ExistenceForm> getExistenceForms() {
    return existenceForms;
  }

  public void setExistenceForms(List<ExistenceForm> existenceForms) {
    this.existenceForms = existenceForms;
  }

  public void setPhysicalLocation(Map<String, String> physicalLocation) {
    this.physicalLocation = physicalLocation;
  }

  public Optional<String> getComment() {
    return Optional.ofNullable(comment);
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Optional<String> getExternalId() {
    return Optional.ofNullable(externalId);
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public Optional<UserProfile> getLastModifiedByUser() {
    return Optional.ofNullable(lastModifiedByUser);
  }

  public void setLastModifiedByUser(UserProfile userProfile) {
    this.lastModifiedByUser = userProfile;
  }

  public Optional<Organization> getOwnerOrganization() {
    return Optional.ofNullable(ownerOrganization);
  }

  public Optional<OrganizationUnit> getOwnerOrganizationUnit() {
    return Optional.ofNullable(ownerOrganizationUnit);
  }

  public List<PersonInRole> getPersonInRoles() {
    return personInRoles;
  }

  public List<AssociatedOrganization> getAssociatedOrganizations() {
    return associatedOrganizations;
  }

  public List<Link> getLinks() {
    return links;
  }

  public Optional<UsageCondition> getUsageCondition() {
    return Optional.ofNullable(usageCondition);
  }

  public Optional<Universe> getUniverse() {
    return Optional.ofNullable(universe);
  }

  public Optional<Population> getPopulation() {
    return Optional.ofNullable(population);
  }

  public Optional<UnitType> getUnitType() {
    return Optional.ofNullable(unitType);
  }

  public Optional<LifecyclePhase> getLifecyclePhase() {
    return Optional.ofNullable(lifecyclePhase);
  }

  public List<Concept> getConceptsFromScheme() {
    return conceptsFromScheme;
  }

  public List<DatasetType> getDatasetTypes() {
    return datasetTypes;
  }

  public Optional<StudyGroup> getStudyGroup() {
    return Optional.ofNullable(studyGroup);
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

  public List<SystemInRole> getSystemInRoles() {
    return systemInRoles;
  }

  public void setSystemInRoles(List<SystemInRole> systemInRoles) {
    this.systemInRoles = systemInRoles;
  }

  public void setOwnerOrganizationUnit(OrganizationUnit ownerOrganizationUnit) {
    this.ownerOrganizationUnit = ownerOrganizationUnit;
  }

  public Boolean isChangedAfterPublish() {
    return changedAfterPublish;
  }

  public void setChangedAfterPublish(Boolean changedAfterPublish) {
    this.changedAfterPublish = changedAfterPublish;
  }

  public List<SupplementaryPhysicalSecurityPrinciple> getOtherPrinciplesForPhysicalSecurity() {
    return otherPrinciplesForPhysicalSecurity;
  }

  public List<SupplementaryDigitalSecurityPrinciple> getOtherPrinciplesForDigitalSecurity() {
    return otherPrinciplesForDigitalSecurity;
  }

  /**
   * Transforms dataset into node
   */
  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    isPublished().ifPresent(v -> props.put("published", toPropertyValue(v)));
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("altLabel", toPropertyValues(altLabel));
    props.putAll("abbreviation", toPropertyValues(abbreviation));
    props.putAll("description", toPropertyValues(description));
    getStudyType().ifPresent(v -> props.put("studyType", PropertyMappings.enumToPropertyValue(v)));
    props.putAll("usageConditionAdditionalInformation", toPropertyValues(usageConditionAdditionalInformation));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));
    getCollectionStartDate().ifPresent(v -> props.put("collectionStartDate", toPropertyValue(v)));
    getCollectionEndDate().ifPresent(v -> props.put("collectionEndDate", toPropertyValue(v)));
    getNumberOfObservationUnits().ifPresent(v -> props.put("numberOfObservationUnits", toPropertyValue(v)));
    props.putAll("freeConcepts", toPropertyValues(freeConcepts));
    getPersonRegistry().ifPresent(v -> props.put("personRegistry", toPropertyValue(v)));
    props.putAll("registryPolicy", toPropertyValues(registryPolicy));
    props.putAll("purposeOfPersonRegistry", toPropertyValues(purposeOfPersonRegistry));
    props.putAll("usageOfPersonalInformation", toPropertyValues(usageOfPersonalInformation));
    props.putAll("personRegistrySources", toPropertyValues(personRegistrySources));
    props.putAll("personRegisterDataTransfers", toPropertyValues(personRegisterDataTransfers));
    props.putAll("personRegisterDataTransfersOutsideEuOrEea", toPropertyValues(personRegisterDataTransfersOutsideEuOrEea));
    props.putAll("legalBasisForHandlingPersonalData", PropertyMappings.enumsToPropertyValues(legalBasisForHandlingPersonalData));
    props.putAll("otherLegalBasisForHandlingPersonalData", toPropertyValues(otherLegalBasisForHandlingPersonalData));
    getContainsSensitivePersonalData().ifPresent(v -> props.put("containsSensitivePersonalData", toPropertyValue(v)));
    props.putAll("legalBasisForHandlingSensitivePersonalData", PropertyMappings.enumsToPropertyValues(legalBasisForHandlingSensitivePersonalData));
    props.putAll("otherLegalBasisForHandlingSensitivePersonalData", toPropertyValues(otherLegalBasisForHandlingSensitivePersonalData));
    props.putAll("typeOfSensitivePersonalData", PropertyMappings.enumsToPropertyValues(typeOfSensitivePersonalData));
    props.putAll("otherTypeOfSensitivePersonalData", toPropertyValues(otherTypeOfSensitivePersonalData));
    getIsScientificStudy().ifPresent(v -> props.put("isScientificStudy", toPropertyValue(v)));
    props.putAll("groupsOfRegistrees", PropertyMappings.enumsToPropertyValues(groupsOfRegistrees));
    props.putAll("otherGroupsOfRegistrees", toPropertyValues(otherGroupsOfRegistrees));
    props.putAll("receivingGroups", PropertyMappings.enumsToPropertyValues(receivingGroups));
    props.putAll("otherReceivingGroups", toPropertyValues(otherReceivingGroups));
    props.putAll("partiesAndSharingOfResponsibilityInCollaborativeStudy", toPropertyValues(partiesAndSharingOfResponsibilityInCollaborativeStudy));
    props.putAll("studyPerformers", toPropertyValues(studyPerformers));
    getProfilingAndAutomation().ifPresent(v -> props.put("profilingAndAutomation", toPropertyValue(v)));
    props.putAll("profilingAndAutomationDescription", toPropertyValues(profilingAndAutomationDescription));
    getDirectIdentityInformation().ifPresent(v -> props.put("directIdentityInformation", toPropertyValue(v)));
    props.putAll("directIdentityInformationDescription", toPropertyValues(directIdentityInformationDescription));

    // Data security
    getConfidentialityClass().ifPresent(cc -> props.put("confidentialityClass", toPropertyValue(cc.toString())));
    props.putAll("groundsForConfidentiality", toPropertyValues(groundsForConfidentiality));
    getSecurityClassification().ifPresent(sc -> props.put("securityClassification", toPropertyValue(sc.toString())));
    props.putAll("principlesForPhysicalSecurity", PropertyMappings.enumsToPropertyValues(principlesForPhysicalSecurity));
    props.putAll("principlesForDigitalSecurity", PropertyMappings.enumsToPropertyValues(principlesForDigitalSecurity));

    // Archiving
    getDataProcessingStartDate().ifPresent(v -> props.put("dataProcessingStartDate", toPropertyValue(v)));
    getDataProcessingEndDate().ifPresent(v -> props.put("dataProcessingEndDate", toPropertyValue(v)));
    getRetentionPolicy().ifPresent(rp -> props.put("retentionPolicy", toPropertyValue(rp.toString())));
    props.putAll("retentionPeriod", toPropertyValues(retentionPeriod));
    props.putAll("groundsForRetention", toPropertyValues(groundsForRetention));
    props.putAll("nationalArchivesFinlandArchivalDecision", toPropertyValues(nationalArchivesFinlandArchivalDecision));
    props.putAll("existenceForms", PropertyMappings.enumsToPropertyValues(existenceForms));
    props.putAll("physicalLocation", toPropertyValues(physicalLocation));
    getComment().ifPresent(v -> props.put("comment", toPropertyValue(v)));
    getExternalId().ifPresent(ei -> props.put("externalId", toPropertyValue(ei)));
    getPostStudyRetentionOfPersonalData().ifPresent(v -> props.put("postStudyRetentionOfPersonalData", toPropertyValue(v.toString())));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getLastModifiedByUser().ifPresent(v -> refs.put("lastModifiedByUser", v.toNode()));
    getOwnerOrganization().ifPresent(oo -> refs.put("ownerOrganization", oo.toNode()));
    getOwnerOrganizationUnit().ifPresent(oou -> refs.put("ownerOrganizationUnit", oou.toNode()));
    getPersonInRoles().forEach(pir -> refs.put("personInRoles", pir.toNode()));
    getAssociatedOrganizations().forEach(org -> refs.put("associatedOrganizations", org.toNode()));
    getLinks().forEach(l -> refs.put("links", l.toNode()));
    getUsageCondition().ifPresent(uc -> refs.put("usageCondition", uc.toNode()));
    getUniverse().ifPresent(u -> refs.put("universe", u.toNode()));
    getPopulation().ifPresent(p -> refs.put("population", p.toNode()));
    getUnitType().ifPresent(ut -> refs.put("unitType", ut.toNode()));
    getLifecyclePhase().ifPresent(lp -> refs.put("lifecyclePhase", lp.toNode()));
    getConceptsFromScheme().forEach(c -> refs.put("conceptsFromScheme", c.toNode()));
    getDatasetTypes().forEach(dt -> refs.put("datasetTypes", dt.toNode()));
    getStudyGroup().ifPresent(sg -> refs.put("studyGroup", sg.toNode()));
    getDatasets().forEach(d -> refs.put("dataSets", d.toNode()));
    getPredecessors().forEach(d -> refs.put("predecessors", d.toNode()));
    getSystemInRoles().forEach(sir -> refs.put("systemInRoles", sir.toNode()));
    getOtherPrinciplesForPhysicalSecurity().forEach(ops -> refs.put("otherPrinciplesForPhysicalSecurity", ops.toNode()));
    getOtherPrinciplesForDigitalSecurity().forEach(ods -> refs.put("otherPrinciplesForDigitalSecurity", ods.toNode()));

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
            && Objects.equals(published, study.published)
            && Objects.equals(prefLabel, study.prefLabel)
            && Objects.equals(altLabel, study.altLabel)
            && Objects.equals(abbreviation, study.abbreviation)
            && Objects.equals(description, study.description)
            && Objects.equals(usageConditionAdditionalInformation, study.usageConditionAdditionalInformation)
            && Objects.equals(referencePeriodStart, study.referencePeriodStart)
            && Objects.equals(referencePeriodEnd, study.referencePeriodEnd)
            && Objects.equals(collectionStartDate, study.collectionStartDate)
            && Objects.equals(collectionEndDate, study.collectionEndDate)
            && Objects.equals(numberOfObservationUnits, numberOfObservationUnits)
            && Objects.equals(freeConcepts, study.freeConcepts)
            && Objects.equals(personRegistry, study.personRegistry)
            && Objects.equals(registryPolicy, study.registryPolicy)
            && Objects.equals(purposeOfPersonRegistry, study.purposeOfPersonRegistry)
            && Objects.equals(usageOfPersonalInformation, study.usageOfPersonalInformation)
            && Objects.equals(personRegistrySources, study.personRegistrySources)
            && Objects.equals(personRegisterDataTransfers, study.personRegisterDataTransfers)
            && Objects.equals(personRegisterDataTransfersOutsideEuOrEea, study.personRegisterDataTransfersOutsideEuOrEea)
            && Objects.equals(legalBasisForHandlingPersonalData, study.legalBasisForHandlingPersonalData)
            && Objects.equals(otherLegalBasisForHandlingPersonalData, study.otherLegalBasisForHandlingPersonalData)
            && Objects.equals(containsSensitivePersonalData, study.containsSensitivePersonalData)
            && Objects.equals(legalBasisForHandlingSensitivePersonalData, study.legalBasisForHandlingSensitivePersonalData)
            && Objects.equals(otherLegalBasisForHandlingSensitivePersonalData, study.otherLegalBasisForHandlingSensitivePersonalData)
            && Objects.equals(typeOfSensitivePersonalData, study.typeOfSensitivePersonalData)
            && Objects.equals(otherTypeOfSensitivePersonalData, study.otherTypeOfSensitivePersonalData)
            && Objects.equals(partiesAndSharingOfResponsibilityInCollaborativeStudy, study.partiesAndSharingOfResponsibilityInCollaborativeStudy)
            && Objects.equals(isScientificStudy, study.isScientificStudy)
            && Objects.equals(profilingAndAutomation, study.profilingAndAutomation)
            && Objects.equals(profilingAndAutomationDescription, study.profilingAndAutomationDescription)
            && Objects.equals(directIdentityInformation, study.directIdentityInformation)
            && Objects.equals(directIdentityInformationDescription, study.directIdentityInformationDescription)
            // Data security
            && Objects.equals(confidentialityClass, study.confidentialityClass)
            && Objects.equals(groundsForConfidentiality, study.groundsForConfidentiality)
            && Objects.equals(securityClassification, study.securityClassification)
            && Objects.equals(principlesForPhysicalSecurity, study.principlesForPhysicalSecurity)
            && Objects.equals(principlesForDigitalSecurity, study.principlesForDigitalSecurity)
            && Objects.equals(otherPrinciplesForPhysicalSecurity, study.otherPrinciplesForPhysicalSecurity)
            && Objects.equals(otherPrinciplesForDigitalSecurity, study.otherPrinciplesForDigitalSecurity)
            // Archiving
            && Objects.equals(dataProcessingStartDate, study.dataProcessingStartDate)
            && Objects.equals(dataProcessingEndDate, study.dataProcessingEndDate)
            && Objects.equals(retentionPolicy, study.retentionPolicy)
            && Objects.equals(retentionPeriod, study.retentionPeriod)
            && Objects.equals(groundsForRetention, study.groundsForRetention)
            && Objects.equals(nationalArchivesFinlandArchivalDecision, study.nationalArchivesFinlandArchivalDecision)
            && Objects.equals(postStudyRetentionOfPersonalData, study.postStudyRetentionOfPersonalData)
            && Objects.equals(existenceForms, study.existenceForms)
            && Objects.equals(physicalLocation, study.physicalLocation)
            && Objects.equals(comment, study.comment)
            && Objects.equals(externalId, study.externalId)
            && Objects.equals(lastModifiedByUser, study.lastModifiedByUser)
            && Objects.equals(ownerOrganization, study.ownerOrganization)
            && Objects.equals(ownerOrganizationUnit, study.ownerOrganizationUnit)
            && Objects.equals(personInRoles, study.personInRoles)
            && Objects.equals(links, study.links)
            && Objects.equals(usageCondition, study.usageCondition)
            && Objects.equals(universe, study.universe)
            && Objects.equals(population, study.population)
            && Objects.equals(unitType, study.unitType)
            && Objects.equals(lifecyclePhase, study.lifecyclePhase)
            && Objects.equals(conceptsFromScheme, study.conceptsFromScheme)
            && Objects.equals(datasetTypes, study.datasetTypes)
            && Objects.equals(studyGroup, study.studyGroup)
            && Objects.equals(datasets, study.datasets)
            && Objects.equals(predecessors, study.predecessors)
            && Objects.equals(systemInRoles, study.systemInRoles);
  }

  @Override
  public int hashCode() {
      return Objects.hash(id,
        lastModifiedDate,
        published,
        prefLabel,
        altLabel,
        abbreviation,
        description,
        studyType,
        usageConditionAdditionalInformation,
        referencePeriodStart,
        referencePeriodEnd,
        collectionStartDate,
        collectionEndDate,
        numberOfObservationUnits,
        freeConcepts,
        personRegistry,
        registryPolicy,
        purposeOfPersonRegistry,
        usageOfPersonalInformation,
        personRegistrySources,
        personRegisterDataTransfers,
        personRegisterDataTransfersOutsideEuOrEea,
        confidentialityClass,
        groundsForConfidentiality,
        securityClassification,
        principlesForPhysicalSecurity,
        principlesForDigitalSecurity,
        legalBasisForHandlingPersonalData,
        otherLegalBasisForHandlingPersonalData,
        containsSensitivePersonalData,
        legalBasisForHandlingSensitivePersonalData,
        otherLegalBasisForHandlingSensitivePersonalData,
        typeOfSensitivePersonalData,
        otherTypeOfSensitivePersonalData,
        partiesAndSharingOfResponsibilityInCollaborativeStudy,
        studyPerformers,
        isScientificStudy,
        dataProcessingStartDate,
        dataProcessingEndDate,
        retentionPolicy,
        existenceForms,
        retentionPeriod,
        groundsForRetention,
        nationalArchivesFinlandArchivalDecision,
        postStudyRetentionOfPersonalData,
        physicalLocation,
        comment,
        externalId,
        lastModifiedByUser,
        ownerOrganization,
        ownerOrganizationUnit,
        personInRoles,
        links,
        usageCondition,
        universe,
        population,
        unitType,
        lifecyclePhase,
        conceptsFromScheme,
        datasetTypes,
        studyGroup,
        datasets,
        predecessors,
        systemInRoles,
        otherPrinciplesForPhysicalSecurity,
        otherPrinciplesForDigitalSecurity
      );
  }

  @JsonIgnore
  public Study getSimplified() {
    Study study = new Study();
    study.id = this.id;
    study.prefLabel = this.prefLabel;
    study.altLabel = this.altLabel;
    study.abbreviation = this.abbreviation;
    study.description = this.description;
    study.studyType = this.studyType;
    study.usageConditionAdditionalInformation = this.usageConditionAdditionalInformation;
    study.referencePeriodStart = this.referencePeriodStart;
    study.referencePeriodEnd = this.referencePeriodEnd;
    study.collectionStartDate = this.collectionStartDate;
    study.collectionEndDate = this.collectionEndDate;
    study.numberOfObservationUnits = this.numberOfObservationUnits;
    study.freeConcepts = this.freeConcepts;
    study.personRegistry = this.personRegistry;
    study.registryPolicy = this.registryPolicy;
    study.purposeOfPersonRegistry = this.purposeOfPersonRegistry;
    study.usageOfPersonalInformation = this.usageOfPersonalInformation;
    study.personRegistrySources = this.personRegistrySources;
    study.personRegisterDataTransfers = this.personRegisterDataTransfers;
    study.personRegisterDataTransfersOutsideEuOrEea = this.personRegisterDataTransfersOutsideEuOrEea;
    study.legalBasisForHandlingPersonalData = this.legalBasisForHandlingPersonalData;
    study.otherLegalBasisForHandlingPersonalData = this.otherLegalBasisForHandlingPersonalData;
    study.containsSensitivePersonalData = this.containsSensitivePersonalData;
    study.legalBasisForHandlingSensitivePersonalData = this.legalBasisForHandlingSensitivePersonalData;
    study.otherLegalBasisForHandlingSensitivePersonalData = this.otherLegalBasisForHandlingSensitivePersonalData;
    study.typeOfSensitivePersonalData = this.typeOfSensitivePersonalData;
    study.otherTypeOfSensitivePersonalData = this.otherTypeOfSensitivePersonalData;
    study.partiesAndSharingOfResponsibilityInCollaborativeStudy = this.partiesAndSharingOfResponsibilityInCollaborativeStudy;
    study.studyPerformers = this.studyPerformers;
    study.isScientificStudy = this.isScientificStudy;
    study.groupsOfRegistrees = this.groupsOfRegistrees;
    study.otherGroupsOfRegistrees = this.otherGroupsOfRegistrees;
    study.receivingGroups = this.receivingGroups;
    study.otherReceivingGroups = this.otherReceivingGroups;
    study.profilingAndAutomation = this.profilingAndAutomation;
    study.profilingAndAutomationDescription = this.profilingAndAutomationDescription;
    study.directIdentityInformation = this.directIdentityInformation;
    study.directIdentityInformationDescription = this.directIdentityInformationDescription;
    // Data security
    study.confidentialityClass = this.confidentialityClass;
    study.groundsForConfidentiality = this.groundsForConfidentiality;
    study.securityClassification = this.securityClassification;
    study.principlesForPhysicalSecurity = this.principlesForPhysicalSecurity;
    study.principlesForDigitalSecurity = this.principlesForDigitalSecurity;

    // Archiving
    study.dataProcessingStartDate = this.dataProcessingStartDate;
    study.dataProcessingEndDate = this.dataProcessingEndDate;
    study.retentionPolicy = this.retentionPolicy;
    study.retentionPeriod = this.retentionPeriod;
    study.groundsForRetention = this.groundsForRetention;
    study.nationalArchivesFinlandArchivalDecision = this.nationalArchivesFinlandArchivalDecision;
    study.postStudyRetentionOfPersonalData = this.postStudyRetentionOfPersonalData;

    study.existenceForms = this.existenceForms;
    study.ownerOrganization = this.ownerOrganization;
    study.ownerOrganizationUnit = this.ownerOrganizationUnit;
    study.links = this.links;
    study.usageCondition = this.usageCondition;
    study.universe = this.universe;
    study.population = this.population;
    study.unitType = this.unitType;
    study.lifecyclePhase = this.lifecyclePhase;
    study.conceptsFromScheme = this.conceptsFromScheme;
    study.datasetTypes = this.datasetTypes;
    study.studyGroup = this.studyGroup;
    study.systemInRoles = this.systemInRoles;

    for (PersonInRole personInRole : this.personInRoles) {
      if (personInRole.isPublic().isPresent() && personInRole.isPublic().get().equals(Boolean.TRUE)) {
        study.personInRoles.add(personInRole);
      }
    }

    for (AssociatedOrganization org : this.associatedOrganizations) {
      study.associatedOrganizations.add(org);
    }

    for (Study predecessor : this.predecessors) {
      if (predecessor.isPublished().isPresent() && predecessor.isPublished().get().equals(Boolean.TRUE)) {
        study.predecessors.add(predecessor.getSimplified());
      }
    }

    for (Dataset dataset : this.datasets) {
      study.datasets.add(dataset.getSimplified());
    }

    return study;
  }
}
