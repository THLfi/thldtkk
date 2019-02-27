package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForDigitalSecurity;
import fi.thl.thldtkk.api.metadata.domain.PrincipleForPhysicalSecurity;
import fi.thl.thldtkk.api.metadata.domain.Study;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.Constants.DEFAULT_LANG;
import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;
import static java.util.Arrays.asList;

public class StudyBuilder {

  private UUID id;
  private Date lastModifiedDate;
  private Map<String, String> prefLabel;
  private Map<String, String> description;
  private Map<String, String> registryPolicy;
  private Map<String, String> purposeOfPersonRegistry;
  private Map<String, String> usageOfPersonalInformation;
  private Map<String, String> personRegistrySources;
  private Map<String, String> personRegisterDataTransfers;
  private Map<String, String> personRegisterDataTransfersOutsideEuOrEea;
  private Map<String, String> partiesAndSharingOfResponsibilityInCollaborativeStudy;
  private List<PrincipleForPhysicalSecurity> principlesForPhysicalSecurity = new ArrayList<>();
  private List<PrincipleForDigitalSecurity> principlesForDigitalSecurity = new ArrayList<>();
  private Organization ownerOrganization;
  private List<PersonInRole> personInRoles = new ArrayList<>();
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

  public StudyBuilder withLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
    return this;
  }

  public StudyBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public StudyBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, prefLabelValue);
    return withPrefLabel(langValues);
  }

  public StudyBuilder withDescription(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public StudyBuilder withDescription(String descriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, descriptionValue);
    return withDescription(langValues);
  }

  public StudyBuilder withRegistryPolicy(Map<String, String> registryPolicy) {
    this.registryPolicy = registryPolicy;
    return this;
  }

  public StudyBuilder withRegistryPolicy(String registryPolicyValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, registryPolicyValue);
    return withRegistryPolicy(langValues);
  }

  public StudyBuilder withPurposeOfPersonRegistry(Map<String, String> purposeOfPersonRegistry) {
    this.purposeOfPersonRegistry = purposeOfPersonRegistry;
    return this;
  }

  public StudyBuilder withPurposeOfPersonRegistry(String purposeOfPersonRegistryValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, purposeOfPersonRegistryValue);
    return withPurposeOfPersonRegistry(langValues);
  }

  public StudyBuilder withUsageOfPersonalInformation(Map<String, String> usageOfPersonalInformation) {
    this.usageOfPersonalInformation = usageOfPersonalInformation;
    return this;
  }

  public StudyBuilder withUsageOfPersonalInformation(String usageOfPersonalInformation) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, usageOfPersonalInformation);
    return withUsageOfPersonalInformation(langValues);
  }

  public StudyBuilder withPersonRegistrySources(Map<String, String> personRegistrySources) {
    this.personRegistrySources = personRegistrySources;
    return this;
  }

  public StudyBuilder withPersonRegistrySources(String personRegistrySourcesValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, personRegistrySourcesValue);
    return withPersonRegistrySources(langValues);
  }

  public StudyBuilder withPersonRegisterDataTransfers(Map<String, String> personRegisterDataTransfers) {
    this.personRegisterDataTransfers = personRegisterDataTransfers;
    return this;
  }

  public StudyBuilder withPersonRegisterDataTransfers(String personRegisterDataTransfersValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, personRegisterDataTransfersValue);
    return withPersonRegisterDataTransfers(langValues);
  }

  public StudyBuilder withPersonRegisterDataTransfersOutsideEuOrEea(Map<String, String> personRegisterDataTransfersOutsideEuOrEea) {
    this.personRegisterDataTransfersOutsideEuOrEea = personRegisterDataTransfersOutsideEuOrEea;
    return this;
  }

  public StudyBuilder withPersonRegisterDataTransfersOutsideEuOrEea(String personRegisterDataTransfersOutsideEuOrEeaValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, personRegisterDataTransfersOutsideEuOrEeaValue);
    return withPersonRegisterDataTransfersOutsideEuOrEea(langValues);
  }


  public StudyBuilder withPartiesAndSharingOfResponsibilityInCollaborativeStudy(Map<String, String> value) {
    this.partiesAndSharingOfResponsibilityInCollaborativeStudy = value;
    return this;
  }

  public StudyBuilder withPartiesAndSharingOfResponsibilityInCollaborativeStudy(String value) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put(DEFAULT_LANG, value);
    return withPartiesAndSharingOfResponsibilityInCollaborativeStudy(langValues);
  }

  public StudyBuilder withPrinciplesForPhysicalSecurity(PrincipleForPhysicalSecurity... principles) {
    this.principlesForPhysicalSecurity = asList(principles);
    return this;
  }

  public StudyBuilder withPrinciplesForDigitalSecurity(PrincipleForDigitalSecurity... principles) {
    this.principlesForDigitalSecurity = asList(principles);
    return this;
  }

  public StudyBuilder withOwnerOrganization(Organization ownerOrganization) {
    this.ownerOrganization = ownerOrganization;
    return this;
  }

  public StudyBuilder withPersonInRoles(PersonInRole pirs) {
    this.personInRoles = asList(pirs);
    return this;
  }

  public StudyBuilder withPredecessors(Study... predecessors) {
    this.predecessors = asList(predecessors);
    return this;
  }

  public StudyBuilder withDatasets(Dataset... datasets) {
    this.datasets = asList(datasets);
    return this;
  }

  public Study build() {
    return new Study(
      id,
      lastModifiedDate,
      prefLabel,
      description,
      registryPolicy,
      purposeOfPersonRegistry,
      usageOfPersonalInformation,
      personRegistrySources,
      personRegisterDataTransfers,
      personRegisterDataTransfersOutsideEuOrEea,
      principlesForPhysicalSecurity,
      principlesForDigitalSecurity,
      partiesAndSharingOfResponsibilityInCollaborativeStudy,
      ownerOrganization,
      personInRoles,
      datasets,
      predecessors
    );
  }

}
