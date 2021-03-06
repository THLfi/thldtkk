package fi.thl.thldtkk.api.metadata.service.termed;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.OrCriteria.or;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Strings;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;

import fi.thl.thldtkk.api.metadata.domain.ConfidentialityClass;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.NotificationSubject;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.NodeEntity;
import fi.thl.thldtkk.api.metadata.domain.NotificationMessageState;
import fi.thl.thldtkk.api.metadata.domain.OrganizationPersonInRole;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.RecipientNotificationState;
import fi.thl.thldtkk.api.metadata.domain.RoleLabel;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.StudyForm;
import fi.thl.thldtkk.api.metadata.domain.StudyFormConfirmationState;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.security.UserWithProfile;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.EmailService;
import fi.thl.thldtkk.api.metadata.service.PublicStudyService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.email.StudyFormEmailTemplateFactory;

public class EditorStudyServiceImpl implements EditorStudyService {

  private static final Logger LOG = LoggerFactory.getLogger(EditorStudyServiceImpl.class);
  private static final String FILE_PATH = "csv/exampleImportVariables.xls";

  private final Repository<NodeId, Node> nodes;
  private final UserHelper userHelper;

  public EditorStudyServiceImpl(Repository<NodeId, Node> nodes,
                                UserHelper userHelper) {
    this.nodes = nodes;
    this.userHelper = userHelper;
  }

  @Autowired
  private PublicStudyService publicStudyService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private StudyFormEmailTemplateFactory emailTemplateFactory;

  @Override
  public List<Study> findAll() {
    return find("", -1);
  }

  @Override
  public List<Study> find(String query, int max) {
    return find(null, query, max, null);
  }

  @Override
  public List<Study> find(UUID organizationId, String query, int max, String sortString) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", Study.TERMED_NODE_CLASS));

    if (!userHelper.isCurrentUserAdmin()) {
      criteria.add(getCurrentUserOrganizationCriteria());
    }

    if (organizationId != null) {
      criteria.add(keyValue("references.ownerOrganization.id", organizationId.toString()));
    }

    if (hasText(query)) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");

      criteria.add(or(keyWithAllValues("properties.prefLabel", tokens),
    	      keyWithAnyValue("r.personInRoles.r.person.p.lastName", tokens),
              keyWithAnyValue("r.personInRoles.r.person.p.firstName", tokens),
              keyWithAnyValue("r.ownerOrganizationUnit.p.prefLabel", tokens),
              keyWithAllValues("r.ownerOrganizationUnit.p.abbreviation", tokens),
              keyWithAllValues("r.dataSets.p.prefLabel", tokens)
    	      ));
    }

    Stream<Node> studyNodes;
    if (hasText(sortString)) {
      studyNodes = this.nodes.query(and(criteria), max, Sort.sort(sortString));
    }
    else {
      studyNodes = this.nodes.query(and(criteria), max);
    }

    return studyNodes.map(Study::new).collect(toList());
  }

  @Override
  public List<Study> getOrganizationStudies(UUID organizationId, List<String> includeFields) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", Study.TERMED_NODE_CLASS));

    if (!userHelper.isCurrentUserAdmin()) {
      criteria.add(getCurrentUserOrganizationCriteria());
    }

    criteria.add(keyValue("references.ownerOrganization.id", organizationId.toString()));

    Stream<Node> studyNodes;
    if (includeFields != null) {
      Select select = new Select(includeFields);
      studyNodes = this.nodes.query(select, and(criteria));
    } else {
      studyNodes = this.nodes.query(and(criteria));
    }

    return studyNodes.map(Study::new).collect(toList());
  }

  private Criteria getCurrentUserOrganizationCriteria() {
    List<String> organizationIds = userHelper.getCurrentUserOrganizations().stream()
      .map(organization -> organization.getId().toString())
      .collect(Collectors.toList());

    return keyWithAnyValue("references.ownerOrganization.id", organizationIds);
  }

  @Override
  public Optional<Study> get(UUID id) {
    return get(id, true);
  }

  @Override
  public Optional<Study> get(UUID id, boolean includeDatasets) {
    Select select;
    if (includeDatasets) {
      select = select(
        "id",
        "type",
        "lastModifiedDate",
        "properties.*",
        "references.*",
        "references.personInRoles:3",
        "references.personInRole:4",
        "references.person:5",
        "references.role:5",
        "references.associatedOrganizations:3",
        "references.organization:4",
        "references.links:3",
        "references.instanceVariable:3",
        "references.variable:3",
        "references.conceptsFromScheme:3",
        "StudyForm.references.notificationStates:2",
        // Disabled because increases load times too much
        //"references.inScheme:4",
        "references.quantity:3",
        "references.unit:3",
        "references.codeList:3",
        "references.codeItems:4",
        "references.source:3",
        "references.unitType:3",
        "references.lastModifiedByUser:3",
        "references.instanceQuestions:3",
        "references.usageCondition:2",
        "references.lifecyclePhase:2",
        "references.population:2",
        "references.universe:2",
        "references.datasetType:2",
        "references.organizationUnit:2",
        "references.unitInCharge:2",
        "references.ownerOrganization:4",
        "references.ownerOrganizationUnit:2",
        "references.system:2",
        "references.systemRole:2",
        "references.link:3",
        "references.predecessors:3",
        "referrers.predecessors:3",
        "referrers.dataSets:3"
      );
    }
    else {
      select = select(
        "id",
        "type",
        "lastModifiedDate",
        "properties.*",
        "references.personInRoles:3",
        "references.personInRole:4",
        "references.role:5",
        "references.person:5",
        "references.associatedOrganizations",
        "references.organization:2",
        "references.systemInRoles",
        "references.system:2",
        "references.systemRole:2",
        "references.links",
        "references.variable",
        "references.conceptsFromScheme",
        "references.quantity",
        "references.unit",
        "references.codeList",
        "references.codeItems",
        "references.source",
        "references.unitType",
        "references.lastModifiedByUser",
        "references.instanceQuestions",
        "references.usageCondition",
        "references.lifecyclePhase",
        "references.population",
        "references.universe",
        "references.datasetTypes",
        "references.ownerOrganization",
        "references.ownerOrganizationUnit",
        "references.organizationUnit:2",
        "references.systemInRoles",
        "references.link",
        "references.predecessors",
        "references.studyGroup",
        "references.unitInCharge:2",
        "references.otherPrinciplesForPhysicalSecurity",
        "references.otherPrinciplesForDigitalSecurity",
        "references.predecessors",
        "references.studyForms",
        "StudyForm.references.notificationStates:2",
        "referrers.predecessors"
      );
    }

    Optional<Study> study = nodes.get(select, new NodeId(id, Study.TERMED_NODE_CLASS)).map(Study::new);

    if (study.isPresent()) {
      checkUserIsAllowedToAccessStudy(study.get());

      if (study.get().isPublished().isPresent() && Boolean.TRUE.equals(study.get().isPublished().get())) {
        Optional<Study> catalogStudy = publicStudyService.get(id);

        if (catalogStudy.isPresent()) {
          if((catalogStudy.get().getLastModifiedDate().get().before(study.get().getLastModifiedDate().get()))) {
            study.get().setChangedAfterPublish(true);
          }
        }
      }
    }

    return study;
  }

  private void checkUserIsAllowedToAccessStudy(Study study) {
    if (userHelper.isCurrentUserAdmin()) {
      // Admins can view and edit studies of any organization
      return;
    }

    if (!study.getOwnerOrganization().isPresent()) {
      throwDatasetAccessException(study, "which has no organization");
    }

    UUID studyOrganizationId = study.getOwnerOrganization().get().getId();
    Set<UUID> userOrganizationIds = userHelper.getCurrentUserOrganizations()
      .stream()
      .map(org -> org.getId())
      .collect(Collectors.toSet());

    if (!userOrganizationIds.contains(studyOrganizationId)) {
      throwDatasetAccessException(study, "because user is not member of study's organization '" + studyOrganizationId + "'");
    }
  }

  private void throwDatasetAccessException(Study study, String cause) {
    throw new AccessDeniedException(
      new StringBuilder()
        .append("User '")
        .append(userHelper.getCurrentUser().get().getUsername())
        .append("' is not allowed to view/save/delete study '")
        .append(study.getId())
        .append("' ")
        .append(cause)
        .toString()
    );
  }

  @Override
  public Optional<Dataset> getDataset(UUID studyId, UUID datasetId) {
    Study study = get(studyId).orElseThrow(entityNotFound(Study.class, studyId));
    return getDataset(study, datasetId);
  }

  private Optional<Dataset> getDataset(Study study, UUID datasetId) {
    return study.getDatasets()
      .stream()
      .filter(d -> d.getId().equals(datasetId))
      .findFirst();
  }

  @Override
  public Study save(Study study) {
    Study savedStudy = saveStudyInternal(study, false, false);

    LOG.info("Saved study '{}' (externalId = '{}')", savedStudy.getId(), savedStudy.getExternalId().get());

    return savedStudy;
  }

  private Study saveStudyInternal(Study study, boolean includeDatasets, boolean includeInstanceVariables) {
    Optional<Study> old;

    if (study.getId() == null) {
      old = empty();
      study.setId(randomUUID());
    } else {
      old = get(study.getId());
    }

    if (!old.isPresent()) {
      checkUserIsAllowedToAccessStudy(study);
    }

    if (studyHasDifferentOwnerOrganizationAsItsStudyGroup(study)) {
        throw new IllegalArgumentException(new StringBuilder()
          .append("Cannot save study '")
          .append(study.getId())
          .append("' because its owner organization doesn't match study group's owner organization")
          .toString());
    }

    if (containsSelf(study, study.getPredecessors())) {
      throw new IllegalArgumentException(
        new StringBuilder()
          .append("Cannot save study '")
          .append(study.getId())
          .append("' because it has a self reference in 'predecessors'")
          .toString());
    }

    if (studyHasDifferentOwnerOrganizationForItsSystems(study)) {
        throw new IllegalArgumentException(new StringBuilder()
          .append("Cannot save study '")
          .append(study.getId())
          .append("' because its owner organization doesn't match the owner organization of its associated systems")
          .toString());
    }

    boolean isStudyPublished = study.isPublished().orElse(false);

    study.getPopulation()
      .ifPresent(p -> p.setId(firstNonNull(p.getId(), randomUUID())));
    study.getLinks()
      .forEach(v -> v.setId(firstNonNull(v.getId(), randomUUID())));
    study.getPersonInRoles()
      .forEach(pir -> pir.setId(firstNonNull(pir.getId(), randomUUID())));
    study.getAssociatedOrganizations()
      .forEach(org -> org.setId(firstNonNull(org.getId(), randomUUID())));
    study.getSystemInRoles()
      .forEach(sir -> sir.setId(firstNonNull(sir.getId(), randomUUID())));
    study.getStudyForms()
      .forEach(form -> form.setId(firstNonNull(form.getId(), randomUUID())));

    if (isNotPersonRegistry(study)) {
      study.setRegistryPolicy(emptyMap());
      study.setPurposeOfPersonRegistry(emptyMap());
      study.setPersonRegistrySources(emptyMap());
      study.setPersonRegisterDataTransfers(emptyMap());
      study.setPersonRegisterDataTransfersOutsideEuOrEta(emptyMap());
    }

    if (isNotClassified(study)) {
      study.setGroundsForConfidentiality(emptyMap());
      study.setSecurityClassification(null);
    }

    if (study.getPrinciplesForPhysicalSecurity().size() > 1) {
      Collections.sort(study.getPrinciplesForPhysicalSecurity());
    }
    if (study.getPrinciplesForDigitalSecurity().size() > 1) {
      Collections.sort(study.getPrinciplesForDigitalSecurity());
    }
    if (study.getExistenceForms().size() > 1) {
      Collections.sort(study.getExistenceForms());
    }

    overrideStudyFormConfirmationState(study, old);

    if (includeDatasets) {
      study.getDatasets()
        .forEach(dataset -> {
          dataset.setLastModifiedByUser(userHelper.getCurrentUser().get().getUserProfile());
          dataset.setId(firstNonNull(dataset.getId(), randomUUID()));
          dataset.setPublished(isStudyPublished);
          dataset.getPopulation()
            .ifPresent(p -> p.setId(firstNonNull(p.getId(), randomUUID())));
          dataset.getLinks()
            .forEach(v -> v.setId(firstNonNull(v.getId(), randomUUID())));
          dataset.getPersonInRoles()
            .forEach(pir -> pir.setId(firstNonNull(pir.getId(), randomUUID())));
          if (includeInstanceVariables) {
            dataset.getInstanceVariables()
              .forEach(iv -> {
                iv.setLastModifiedByUser(userHelper.getCurrentUser().get().getUserProfile());
                iv.setId(firstNonNull(iv.getId(), randomUUID()));
                iv.setPublished(isStudyPublished);
                if (InstanceVariable.VALUE_DOMAIN_TYPE_DESCRIBED.equals(iv.getValueDomainType().orElse(null))) {
                  iv.setCodeList(null);
                }
                else if (InstanceVariable.VALUE_DOMAIN_TYPE_ENUMERATED.equals(iv.getValueDomainType().orElse(null))) {
                  iv.setQuantity(null);
                  iv.setUnit(null);
                  iv.setValueRangeMax(null);
                  iv.setValueRangeMin(null);
                }
              });
          }
        });
    }

    study.setLastModifiedByUser(userHelper.getCurrentUser().get().getUserProfile());
    sendEmail(study, old.orElse(null));

    Changeset<NodeId, Node> changeset;
    if (!old.isPresent()) {
      changeset = changesetForInsert(study, includeDatasets, includeInstanceVariables);
    }
    else {
      changeset = changesetForUpdate(study, old.get(), includeDatasets, includeInstanceVariables);
    }

    nodes.post(changeset);

    return get(study.getId())
      .orElseThrow(studyNotFoundAfterSave(study.getId()));
  }

  private boolean studyHasDifferentOwnerOrganizationAsItsStudyGroup(Study study) {
    if (study.getStudyGroup().isPresent()) {
      Optional<UUID> studyOwnerOrganizationId = study.getOwnerOrganization()
        .map(o -> o.getId());
      Optional<UUID> studyGroupOwnerOrganizationId = study.getStudyGroup()
        .flatMap(sg -> sg.getOwnerOrganization())
        .map(o -> o.getId());
      return !studyOwnerOrganizationId.equals(studyGroupOwnerOrganizationId);
    }
    else {
      return false;
    }
  }

   private boolean studyHasDifferentOwnerOrganizationForItsSystems(Study study) {
    if (!study.getSystemInRoles().isEmpty()) {
      Optional<UUID> studyOwnerOrganizationId = study.getOwnerOrganization()
        .map(o -> o.getId());

      Set<Optional<UUID>> systemOrganizationIds = study.getSystemInRoles()
              .stream()
              .map(systemInRole -> systemInRole.getSystem())
              .flatMap(system -> system.map(Stream::of).orElseGet(Stream::empty))
              .map(system -> system.getOwnerOrganization())
              .flatMap(organization -> organization.map(Stream::of).orElseGet(Stream::empty))
              .map(organization -> Optional.of(organization.getId()))
              .collect(Collectors.toSet());

      return !systemOrganizationIds.contains(studyOwnerOrganizationId) || systemOrganizationIds.size() > 1;
    }
    else {
      return false;
    }
  }

  private <T extends NodeEntity> boolean containsSelf(T node, List<T> nodeRelations) {
    return nodeRelations != null ? nodeRelations.stream()
        .filter(s -> node.getId().equals(s.getId()))
        .findFirst()
        .isPresent()
      : false;
  }

  private boolean isNotPersonRegistry(Study study) {
    return !study.getPersonRegistry().orElse(false);
  }

  private boolean isNotClassified(Study study) {
    switch (study.getConfidentialityClass().orElse(ConfidentialityClass.PUBLIC)) {
      case PARTLY_CONFIDENTIAL:
      case CONFIDENTIAL:
        return false;
      default:
        return true;
    }
  }

  private Changeset<NodeId, Node> changesetForInsert(Study study,
                                                     boolean includeDatasets,
                                                     boolean includeInstanceVariables) {
    Changeset changeset = Changeset.empty();

    if (includeDatasets) {
      for (Dataset dataset : study.getDatasets()) {
        changeset = changeset.merge(changesetForInsert(dataset, includeInstanceVariables));
      }
    }
    else {
      study.setDatasets(Collections.emptyList());
    }

    List<Node> save = new ArrayList<>();
    save.add(study.toNode());
    study.getPopulation().ifPresent(p -> save.add(p.toNode()));
    study.getLinks().forEach(l -> save.add(l.toNode()));
    study.getPersonInRoles().forEach(pir -> save.add(pir.toNode()));
    study.getAssociatedOrganizations().forEach(org -> save.add(org.toNode()));
    study.getSystemInRoles().forEach(sir -> save.add(sir.toNode()));
    study.getStudyForms().forEach(form -> save.add(form.toNode()));
    changeset = changeset.merge(new Changeset(Collections.emptyList(), save));

    return changeset;
  }

  private Changeset<NodeId, Node> changesetForInsert(Dataset dataset, boolean includeInstanceVariables) {
    List<Node> save = new ArrayList<>();

    save.add(dataset.toNode());

    dataset.getPopulation().ifPresent(v -> save.add(v.toNode()));
    dataset.getLinks().forEach(v -> save.add(v.toNode()));
    dataset.getPersonInRoles().forEach(pir -> save.add(pir.toNode()));

    if (includeInstanceVariables) {
      dataset.getInstanceVariables().forEach(iv -> {
         save.add(iv.toNode());
         iv.getInstanceQuestions().forEach(iq -> save.add(iq.toNode()));
      });
    }

    return new Changeset(Collections.emptyList(), save);
  }

  private Changeset<NodeId, Node> changesetForUpdate(Study newStudy,
                                                     Study oldStudy,
                                                     boolean includeDatasets, boolean includeInstanceVariables) {
    List<StudyForm> deletedStudyForms =
     Changeset.getDeletedNodes(newStudy.getStudyForms(), oldStudy.getStudyForms());

    Changeset<NodeId, Node> studyChangeset = Changeset.<NodeId, Node>empty()
      .merge(buildChangeset(
        newStudy.getPopulation().orElse(null),
        oldStudy.getPopulation().orElse(null)))
      .merge(Changeset.buildChangeset(
        newStudy.getLinks(),
        oldStudy.getLinks()))
      .merge(Changeset.buildChangeset(
        newStudy.getPersonInRoles(),
        oldStudy.getPersonInRoles()))
      .merge(Changeset.buildChangeset(
        newStudy.getAssociatedOrganizations(),
        oldStudy.getAssociatedOrganizations()))
      .merge(Changeset.buildChangeset(
        newStudy.getSystemInRoles(),
        oldStudy.getSystemInRoles()))
      .merge(Changeset.buildChangeset(
        newStudy.getStudyForms(),
        oldStudy.getStudyForms()))
      .merge(Changeset.buildChangeset(
        // Save new notificationstates, but only delete if studyform is removed
        // (prevents race conditions if edited by multiple users/scheduler)
        newStudy.getStudyForms().stream()
          .map(StudyForm::getNotificationStates)
          .flatMap(List::stream)
         .collect(Collectors.toList()),
        deletedStudyForms.stream()
          .map(StudyForm::getNotificationStates)
          .flatMap(List::stream)
          .collect(Collectors.toList())
      ));

    if (includeDatasets) {
      // Dataset updates
      Set<UUID> newDatasetIds = new HashSet<>();
      for (Dataset newDataset : newStudy.getDatasets()) {
        newDatasetIds.add(newDataset.getId());

        Optional<Dataset> oldDataset = getDataset(oldStudy, newDataset.getId());
        Changeset<NodeId, Node> datasetChangeset;
        if (oldDataset.isPresent()) {
          datasetChangeset = changesetForUpdate(newDataset, oldDataset.get(), includeInstanceVariables);
        }
        else {
          datasetChangeset = changesetForInsert(newDataset, includeInstanceVariables);
        }
        studyChangeset = studyChangeset.merge(datasetChangeset);
      }

      // Datasets that need to be deleted
      List<Node> datasetRelatedNodesToDelete = new LinkedList<>();
      oldStudy.getDatasets().forEach(oldDataset -> {
        if (!newDatasetIds.contains(oldDataset.getId())) {
          datasetRelatedNodesToDelete.addAll(getDatasetRelatedNodesForDelete(oldDataset));
        }
      });
      studyChangeset = studyChangeset.merge(new Changeset<>(
        datasetRelatedNodesToDelete.stream()
          .map(NodeId::new)
          .collect(Collectors.toList())));
    }
    else {
      // Preserve study's existing datasets
      newStudy.setDatasets(oldStudy.getDatasets());
    }

    studyChangeset = studyChangeset.merge(Changeset.save(newStudy.toNode()));

    return studyChangeset;
  }

  private Collection<Node> getDatasetRelatedNodesForDelete(Dataset dataset) {
    List<Node> nodes = new LinkedList<>();
    nodes.add(dataset.toNode());
    dataset.getPopulation().ifPresent(p -> nodes.add(p.toNode()));
    dataset.getLinks().forEach(l -> nodes.add(l.toNode()));
    dataset.getPersonInRoles().forEach(pir -> nodes.add(pir.toNode()));
    dataset.getInstanceVariables().forEach(iv -> {
      nodes.add(iv.toNode());
      iv.getInstanceQuestions().forEach(iq -> nodes.add(iq.toNode()));
    });
    return nodes;
  }

  private Changeset<NodeId, Node> changesetForUpdate(Dataset newDataset,
                                                     Dataset oldDataset,
                                                     boolean includeInstanceVariables) {
    Changeset<NodeId, Node> changeset = Changeset.<NodeId, Node>save(newDataset.toNode())
      .merge(buildChangeset(
        newDataset.getPopulation().orElse(null),
        oldDataset.getPopulation().orElse(null)))
      .merge(Changeset.buildChangeset(
        newDataset.getLinks(),
        oldDataset.getLinks()))
      .merge(Changeset.buildChangeset(
        newDataset.getPersonInRoles(),
        oldDataset.getPersonInRoles()));
    if (includeInstanceVariables) {
      changeset = changeset.merge(Changeset.buildChangeset(
        newDataset.getInstanceVariables(),
        oldDataset.getInstanceVariables()))
          .merge(buildChangesetForInstanceQuestions(
                  newDataset.getInstanceVariables(),
                  oldDataset.getInstanceVariables()));
    }
    return changeset;
  }

  private Changeset<NodeId, Node> buildChangeset(Population newPopulation,
                                                 Population oldPopulation) {
    if (newPopulation != null && oldPopulation == null) {
      return Changeset.save(newPopulation.toNode());
    }
    // save with existing id
    if (newPopulation != null && !newPopulation.equals(oldPopulation)) {
      return Changeset.save(new Population(
        oldPopulation.getId(),
        newPopulation.getPrefLabel(),
        newPopulation.getSampleSize(),
        newPopulation.getLoss(),
        newPopulation.getGeographicalCoverage()).toNode());
    }
    if (newPopulation == null && oldPopulation != null) {
      return Changeset.delete(new NodeId(oldPopulation.toNode()));
    }
    return Changeset.empty();
  }

 private Changeset<NodeId, Node> buildChangesetForInstanceQuestions(List<InstanceVariable> newInstanceVariables,
                                                 List<InstanceVariable> oldInstanceVariables) {

    List<InstanceQuestion> newInstanceQuestions = newInstanceVariables
            .stream()
            .flatMap(niv -> niv.getInstanceQuestions().stream())
            .distinct()
            .collect(Collectors.toList());

    List<InstanceQuestion> oldInstanceQuestions = oldInstanceVariables
            .stream()
            .flatMap(oiv -> oiv.getInstanceQuestions().stream())
            .distinct()
            .collect(Collectors.toList());

    return Changeset.<NodeId, Node>empty().merge(
                    Changeset.buildChangeset(newInstanceQuestions, oldInstanceQuestions));
  }

  private Supplier<IllegalStateException> studyNotFoundAfterSave(UUID studyId) {
    return () -> new IllegalStateException("Study '" + studyId
      + "' was not found after saving, it might have been updated simultaneously by another user");
  }

  private void sendEmail(Study study, Study old) {
    sendUnitInChargeConfirmationEmail(study, old);
    sendRetentionPeriodConfirmationEmail(study, old);
  }

  private static Optional<StudyForm> findMatchingStudyForm(Study study, UUID studyFormId) {
    return study.getStudyForms().stream()
      .filter(iterOldStudyForm -> iterOldStudyForm.getId().equals(studyFormId))
      .findFirst();
  }

  private Predicate<StudyForm> unitInChargeHasChangedBetweenStudies(Study study, Study comparedStudy) {
    return studyForm -> {
      if (comparedStudy == null) {
        return true;
      }

      Optional<StudyForm> oldStudyForm = findMatchingStudyForm(comparedStudy, study.getId());

      if (!oldStudyForm.isPresent() || !oldStudyForm.get().getUnitInCharge().isPresent()) {
        return true;
      }

      UUID newId = studyForm.getUnitInCharge().get().getId();
      UUID oldId = oldStudyForm.get().getUnitInCharge().get().getId();
      return !newId.equals(oldId);
    };
  }

  private void sendUnitInChargeConfirmationEmail(Study study, Study old) {
    Predicate<StudyForm> unitInChargeHasChanged = unitInChargeHasChangedBetweenStudies(study, old);
    Predicate<StudyForm> unitInChargeConfirmationIsPending =
      studyForm -> Objects.equals(studyForm.getUnitInChargeConfirmationState().orElse(null), StudyFormConfirmationState.PENDING);

    study.getStudyForms().stream()
      .filter(IsStudyFormSamplePredicate.INSTANCE)
      .filter(unitInChargeConfirmationIsPending)
      .filter(studyForm -> studyForm.getUnitInCharge().isPresent())
      .filter(unitInChargeHasChanged)
      .map(studyForm -> cleanStudyFormNotificationStates(studyForm, NotificationSubject.UNIT_IN_CHARGE_CONFIRMATION))
      .forEach(studyForm -> {
        OrganizationUnit unitInCharge = studyForm.getUnitInCharge().get();
        List<OrganizationPersonInRole> personsToBeNotified = unitInCharge.getPersonInRoles().stream()
          .filter(isInRole(RoleLabel.HEAD_OF_ORGANIZATION))
          .filter(pir -> pir.getPerson().getEmail().isPresent())
          .collect(Collectors.toList());

        List<Person> recipients = personsToBeNotified.stream()
          .map(OrganizationPersonInRole::getPerson)
          .collect(Collectors.toList());

        emailService.sendEmails(
          recipients,
          emailTemplateFactory.makeUnitInChargeConfirmationMessage(study, unitInCharge)
          );

        List<RecipientNotificationState> notifications = personsToBeNotified.stream()
          .map(personInRole -> {
            RecipientNotificationState notification = new RecipientNotificationState(randomUUID());
            notification.setPersonInRole(personInRole);
            notification.setNotificationState(NotificationMessageState.SENT);
            notification.setSubject(NotificationSubject.UNIT_IN_CHARGE_CONFIRMATION);
            return notification;
          }).collect(Collectors.toList());
        studyForm.getNotificationStates().addAll(notifications);
      });
  }

  private Predicate<StudyForm> retentionPeriodHasChangedBetweenStudies(Study study, Study comparedStudy) {
    return  studyForm -> {
        if (comparedStudy == null) {
          return true;
        }

        Optional<StudyForm> oldStudyForm = findMatchingStudyForm(comparedStudy, studyForm.getId());

        // Send email if retention period was not set before
        if (!oldStudyForm.isPresent() || !oldStudyForm.get().getRetentionPeriod().isPresent()) {
          return true;
        }

        LocalDate newRetentionPeriod = studyForm.getRetentionPeriod().get();
        LocalDate oldRetentionPeriod = oldStudyForm.get().getRetentionPeriod().get();

        // Send email if retention period differs from the current one
        return !newRetentionPeriod.equals(oldRetentionPeriod);
    };
  }

  private void sendRetentionPeriodConfirmationEmail(Study study, Study old) {
    Predicate<StudyForm> retentionPeriodHasChanged = retentionPeriodHasChangedBetweenStudies(study, old);
    Predicate<StudyForm> retentionPeriodConfirmationIsPending =
      studyForm ->
        studyForm.getRetentionPeriod().isPresent() && studyForm.getRetentionPeriodConfirmationState().get().equals(StudyFormConfirmationState.PENDING);

    Map<UUID, Organization> organizationsByOrganizationUnitId = organizationService.findAll().stream()
      .flatMap(o -> o.getOrganizationUnits().stream().map(ou -> Pair.of(ou.getId(), o)))
      .collect(toMap(Pair::getKey, Pair::getValue));

    study.getStudyForms().stream()
      .filter(IsStudyFormSamplePredicate.INSTANCE)
      .filter(studyForm -> studyForm.getUnitInCharge().isPresent())
      .filter(retentionPeriodHasChanged)
      .filter(retentionPeriodConfirmationIsPending)
      .map(studyForm -> cleanStudyFormNotificationStates(studyForm, NotificationSubject.STUDY_FORM_RETENTION_CONFIRMATION))
      .forEach(sampleStudyForm -> {
        OrganizationUnit unitInCharge = sampleStudyForm.getUnitInCharge().get();
        Organization organizationInCharge = organizationsByOrganizationUnitId.get(unitInCharge.getId());

        List<OrganizationPersonInRole> personsToBeNotified = organizationInCharge.getPersonInRoles().stream()
          .filter(isInRole(RoleLabel.HEAD_OF_SAMPLE_MANAGEMENT).or(isInRole(RoleLabel.SAMPLE_MANAGEMENT_COORDINATOR)))
          .filter(pir -> pir.getPerson().getEmail().isPresent())
          .collect(Collectors.toList());

        List<Person> recipients = personsToBeNotified.stream()
          .map(OrganizationPersonInRole::getPerson)
          .collect(Collectors.toList());

        emailService.sendEmails(
          recipients,
          emailTemplateFactory.makeRetentionPeriodConfirmationMessage(study)
          );

        List<RecipientNotificationState> notifications = personsToBeNotified.stream()
          .map(personInRole -> {
            RecipientNotificationState notification = new RecipientNotificationState(randomUUID());
            notification.setPersonInRole(personInRole);
            notification.setNotificationState(NotificationMessageState.SENT);
            notification.setSubject(NotificationSubject.STUDY_FORM_RETENTION_CONFIRMATION);
            return notification;
          }).collect(Collectors.toList());

        sampleStudyForm.getNotificationStates().addAll(notifications);
      });
  }


  @AdminOnly
  @Override
  public void delete(UUID id) {
    Study study = get(id).orElseThrow(entityNotFound(Study.class, id));

    List<Node> delete = new ArrayList<>();

    delete.add(study.toNode());
    study.getPopulation().ifPresent(v -> delete.add(v.toNode()));
    study.getLinks().forEach(v -> delete.add(v.toNode()));
    study.getPersonInRoles().forEach(pir -> delete.add(pir.toNode()));
    study.getAssociatedOrganizations().forEach(org -> delete.add(org.toNode()));
    study.getSystemInRoles().forEach(sir -> delete.add(sir.toNode()));
    study.getDatasets().forEach(dataset -> delete.addAll(getDatasetRelatedNodesForDelete(dataset)));

    nodes.delete(delete.stream().map(NodeId::new).collect(toList()));
  }

  @AdminOnly
  @Override
  public List<Study> getOrganizationUnitStudies(UUID organizationUnitId) {
    List<Study> list =  nodes.query(
      select("id", "type", "properties.published", "properties.prefLabel"),
      and(keyValue("type.id", Study.TERMED_NODE_CLASS),
        keyValue("references.ownerOrganizationUnit.id", organizationUnitId.toString())))
      .map(Study::new)
      .collect(toList());
    return list;
  }

  @AdminOnly
  @Override
  public List<Study> getStudiesByUniverse(UUID universeId) {
    List<Study> list =  nodes.query(
      select("id", "type", "properties.published", "properties.prefLabel"),
      and(keyValue("type.id", Study.TERMED_NODE_CLASS),
        keyValue("references.universe.id", universeId.toString())))
      .map(Study::new)
      .collect(toList());
    return list;
  }

  @AdminOnly
  @Override
  public List<Study> getStudiesByUnitType(UUID unitTypeId) {
    List<Study> list =  nodes.query(
      select("id", "type", "properties.published", "properties.prefLabel"),
      and(keyValue("type.id", Study.TERMED_NODE_CLASS),
        keyValue("references.unitType.id", unitTypeId.toString())))
      .map(Study::new)
      .collect(toList());
    return list;
  }

  @Override
  public Dataset saveDataset(UUID studyId, Dataset dataset) {
    return saveDatasetInternal(studyId, dataset, false);
  }

  private Dataset saveDatasetInternal(UUID studyId, Dataset dataset, boolean saveInstanceVariables) {
    dataset.setId(firstNonNull(dataset.getId(), randomUUID()));

    Study study = get(studyId).orElseThrow(entityNotFound(Study.class, studyId));

    if (containsSelf(dataset, dataset.getPredecessors())) {
      throw new IllegalArgumentException(
        new StringBuilder()
          .append("Cannot save dataset '")
          .append(dataset.getId())
          .append("' because it has a self reference in 'predecessors'")
          .toString());
    }

    Optional<Dataset> existingDataset = getDataset(study, dataset.getId());

    if (existingDataset.isPresent()) {
      if (!saveInstanceVariables) {
        // Preserve dataset's current instance variables, only update other fields.
        dataset.setInstanceVariables(existingDataset.get().getInstanceVariables());
      }
      int index = study.getDatasets().indexOf(existingDataset.get());
      study.getDatasets().remove(index);
      study.getDatasets().add(index, dataset);
    }
    else {
      if (!saveInstanceVariables) {
        dataset.setInstanceVariables(Collections.emptyList());
      }
      study.getDatasets().add(dataset);
    }

    // Always save dataset through study so that study's last modified
    // timestamp gets updated.
    Study savedStudy = saveStudyInternal(study, true, saveInstanceVariables);

    if (saveInstanceVariables) {
      LOG.info("Saved dataset '{}' with {} instance variables", dataset.getId(), dataset.getInstanceVariables().size());
    }
    else {
      LOG.info("Saved dataset '{}'", dataset.getId());
    }

    return getDataset(savedStudy, dataset.getId())
      .orElseThrow(datasetNotFoundAfterSave(dataset.getId(), studyId));
  }

  @Override
  public Dataset saveDatasetAndInstanceVariables(UUID studyId, Dataset dataset) {
    return saveDatasetInternal(studyId, dataset, true); }

  private Supplier<IllegalStateException> datasetNotFoundAfterSave(UUID datasetId, UUID studyId) {
    return () -> new IllegalStateException("Dataset '" + datasetId + "' was not found after saving, study '"
      + studyId + "' might have been updated simultaneously by another user");
  }

  @AdminOnly
  @Override
  public void deleteDataset(UUID studyId, UUID datasetId) {
    Study study = get(studyId)
      .orElseThrow(entityNotFound(Study.class, studyId));
    Dataset dataset = getDataset(study, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId));

    study.getDatasets().remove(dataset);

    saveStudyInternal(study, true, false);

    LOG.info("Deleted dataset '{}' (of study '{}')", datasetId, studyId);
  }

  @Override
  public Optional<InstanceVariable> getInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId) {
    Dataset dataset = getDataset(studyId, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId));
    return getInstanceVariable(dataset, instanceVariableId);
  }

  private Optional<InstanceVariable> getInstanceVariable(Dataset dataset, UUID instanceVariableId) {
    return dataset.getInstanceVariables()
      .stream()
      .filter(iv -> iv.getId().equals(instanceVariableId))
      .findFirst();
  }

  @Override
  public InstanceVariable saveInstanceVariable(UUID studyId, UUID datasetId, InstanceVariable instanceVariable) {
    instanceVariable.setId(firstNonNull(instanceVariable.getId(), randomUUID()));

    Study study = get(studyId).orElseThrow(entityNotFound(Study.class, studyId));
    Dataset dataset = getDataset(study, datasetId).orElseThrow(entityNotFound(Dataset.class, datasetId));

    Optional<InstanceVariable> existingInstanceVariable = getInstanceVariable(dataset, instanceVariable.getId());

    if (existingInstanceVariable.isPresent()) {
      int index = dataset.getInstanceVariables().indexOf(existingInstanceVariable.get());
      dataset.getInstanceVariables().remove(index);
      dataset.getInstanceVariables().add(index, instanceVariable);
    }
    else {
      dataset.getInstanceVariables().add(instanceVariable);
    }

    Study savedStudy = saveStudyInternal(study, true, true);

    LOG.info("Saved instance variable '{}' (of dataset '{}' of study '{}')",
      instanceVariable.getId(), datasetId, studyId);

    Dataset savedDataset = getDataset(savedStudy, datasetId)
      .orElseThrow(datasetNotFoundAfterSave(datasetId, studyId));

    return getInstanceVariable(savedDataset, instanceVariable.getId())
      .orElseThrow(instanceVariableNotFoundAfterSave(instanceVariable.getId(), datasetId, studyId));
  }

  private Supplier<IllegalStateException> instanceVariableNotFoundAfterSave(UUID instanceVariableId,
                                                                            UUID datasetId,
                                                                            UUID studyId) {
    return () -> new IllegalStateException("InstanceVariable '"
      + instanceVariableId
      + "' was not found after saving, dataset '"
      + datasetId
      + "' or study '"
      + studyId
      + "' might have been updated simultaneously by another user");
  }


  @Override
  public void deleteInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId) {
    Study study = get(studyId)
      .orElseThrow(entityNotFound(Study.class, studyId));
    Dataset dataset = getDataset(study, datasetId)
      .orElseThrow(entityNotFound(Dataset.class, datasetId));
    InstanceVariable instanceVariable = getInstanceVariable(dataset, instanceVariableId)
      .orElseThrow(entityNotFound(InstanceVariable.class, instanceVariableId));

    dataset.getInstanceVariables().remove(instanceVariable);

    saveStudyInternal(study, true, true);

    LOG.info("Deleted instance variable '{}' (of dataset '{}' of study '{}')",
      instanceVariable.getId(), datasetId, studyId);
  }

  @Override
  public String getPreviousInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId) {
    Optional<Dataset> dataset = getDataset(studyId, datasetId);
    UUID previousInstanceId = instanceVariableId;

    if (dataset.isPresent()) {
      List<InstanceVariable> instanceVariables = dataset.get().getInstanceVariables();
      Optional<InstanceVariable> matchingVariable = instanceVariables.stream().filter(var -> var.getId().equals(instanceVariableId)).findFirst();

      if (matchingVariable.isPresent()) {
        int currentIndex = instanceVariables.indexOf(matchingVariable.get());
        if (currentIndex > 0) {
          // Set previous item
          previousInstanceId = instanceVariables.get(currentIndex - 1).getId();
        } else {
          // Loop back to the last item
          previousInstanceId = instanceVariables.get(instanceVariables.size() - 1).getId();
        }
      }
    }

    return previousInstanceId.toString();
  }

  @Override
  public String getNextInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId) {

    Optional<Dataset> dataset = getDataset(studyId, datasetId);

    if (dataset.isPresent()) {
      List<InstanceVariable> instanceVariables = dataset.get().getInstanceVariables();

      for (InstanceVariable instanceVariable : instanceVariables) {
        if (instanceVariable.getId().equals(instanceVariableId)) {
          int currentIndex = instanceVariables.indexOf(instanceVariable);
          currentIndex++;

          if (currentIndex == instanceVariables.size()) {
            currentIndex = 0;
          }
          return instanceVariables.get(currentIndex).getId().toString();
        }
      }
    }
    return instanceVariableId.toString();
  }

  @Override
  public HttpEntity<byte[]> getExampleInstanceVariablesCsv(String encoding) throws IOException, URISyntaxException {
    Path path = Paths.get(getClass().getClassLoader()
            .getResource(FILE_PATH).toURI());
    byte[] document = Files.readAllBytes(path);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Disposition", "attachment; filename=example-import-variables.xls");
    headers.set("Content-Type", "text/csv; charset=" + encoding);

    return new HttpEntity<>(document, headers);
  }

  private void overrideStudyFormConfirmationState(Study study, Optional<Study> oldStudy) {
    study.getStudyForms().stream().forEach(studyForm -> {
      Optional<StudyForm> oldStudyForm = Optional.empty();
      if (oldStudy.isPresent()) {
        oldStudyForm = oldStudy.get().getStudyForms().stream()
          .filter(sf -> sf.getId().equals(studyForm.getId()))
          .findFirst();
      }
      overrideUnitInChargeValues(studyForm, oldStudyForm);
      overrideRetentionPeriodConfirmationState(studyForm, oldStudyForm);
    });
  }

  private void overrideUnitInChargeValues(StudyForm studyForm, Optional<StudyForm> oldStudyForm) {
      UserWithProfile user = userHelper.getCurrentUser().get();

      Optional<OrganizationUnit> existingUnit = Optional.empty();
      if (oldStudyForm.isPresent()) {
        existingUnit = oldStudyForm.get().getUnitInCharge();
      }

      Optional<OrganizationUnit> newUnit = studyForm.getUnitInCharge();

      if (!newUnit.isPresent()) {
        // No unit is set
        studyForm.setUnitInChargeConfirmationState(StudyFormConfirmationState.PENDING);

        return;
      }

      if (!existingUnit.isPresent() || !existingUnit.get().getId().equals(newUnit.get().getId())) {
        // Earlier unit is different from the new unit or doesn't exist
        studyForm.setUnitInChargeConfirmationState(StudyFormConfirmationState.PENDING);
        List<RecipientNotificationState> validNotifications =
          removeNotificationsWithSubject(studyForm.getNotificationStates(), NotificationSubject.UNIT_IN_CHARGE_CONFIRMATION);
        studyForm.setNotificationStates(validNotifications);

        return;
      }

      if (!(userHelper.isCurrentUserAdmin() || isUserHeadOfOrganizationUnit(user, newUnit.get()))) {
        // User is not authorized to alter confirmation state
        setEarlierStudyFormStateOrPending(oldStudyForm, studyForm);

        return;
      }

      // Allow values to pass unaltered
  }

  private void overrideRetentionPeriodConfirmationState(StudyForm studyForm, Optional<StudyForm> oldStudyForm) {
      UserWithProfile user = userHelper.getCurrentUser().get();

      Optional<LocalDate> existingRetentionPeriod = Optional.empty();
      if (oldStudyForm.isPresent()) {
        existingRetentionPeriod = oldStudyForm.get().getRetentionPeriod();
      }

      Optional<LocalDate> newRetentionPeriod = studyForm.getRetentionPeriod();

      if (!newRetentionPeriod.isPresent()) {
        // No retention period is set
        studyForm.setRetentionPeriodConfirmationState(StudyFormConfirmationState.PENDING);

        return;
      }

      if (!existingRetentionPeriod.isPresent() || !existingRetentionPeriod.get().equals(newRetentionPeriod.get())) {
        // Earlier retention period is different from the new or doesn't exist
        studyForm.setRetentionPeriodConfirmationState(StudyFormConfirmationState.PENDING);
        List<RecipientNotificationState> validNotifications =
          removeNotificationsWithSubject(studyForm.getNotificationStates(), NotificationSubject.STUDY_FORM_EXPIRATION);
        studyForm.setNotificationStates(validNotifications);

        return;
      }

      Optional<OrganizationUnit> organizationUnit = studyForm.getUnitInCharge();
      if (!(userHelper.isCurrentUserAdmin() || (organizationUnit.isPresent() && isUserHeadOfOrganizationUnit(user, organizationUnit.get())))) {
        // User is not authorized to alter confirmation state
        setEarlierStudyFormStateOrPending(oldStudyForm, studyForm);

        return;
      }

      // Allow values to pass unaltered
  }

  private List<RecipientNotificationState> removeNotificationsWithSubject(List<RecipientNotificationState> notifications, NotificationSubject subject) {
    return notifications.stream()
      .filter(notification -> Objects.equals(notification.getSubject().orElse(null), subject))
      .collect(Collectors.toList());
  }

  private void setEarlierStudyFormStateOrPending(Optional<StudyForm> oldStudyForm, StudyForm newStudyForm) {
    if (oldStudyForm.isPresent()) {
      StudyForm existingStudyForm = oldStudyForm.get();
      newStudyForm.setRetentionPeriodConfirmationState(
        existingStudyForm.getRetentionPeriodConfirmationState().orElse(StudyFormConfirmationState.PENDING));
      newStudyForm.setUnitInChargeConfirmationState(
        existingStudyForm.getUnitInChargeConfirmationState().orElse(StudyFormConfirmationState.PENDING));
    } else {
      newStudyForm.setRetentionPeriodConfirmationState(StudyFormConfirmationState.PENDING);
      newStudyForm.setUnitInChargeConfirmationState(StudyFormConfirmationState.PENDING);
    }
  }

  private static final boolean isUserHeadOfOrganizationUnit(UserWithProfile user, OrganizationUnit unit) {
    String userEmail = user.getUserProfile().getEmail().orElse(null);

    if (Strings.isNullOrEmpty(userEmail)) {
      // Unable to link user to head of organization without email
      return false;
    }

    return unit.getPersonInRoles().stream()
      .filter(pir -> pir.getRole().getLabel().equals(RoleLabel.HEAD_OF_ORGANIZATION))
      .filter(pir -> Objects.equals(pir.getPerson().getEmail().orElse(null), userEmail))
      .findFirst()
      .isPresent();
  }

  private static final Predicate<OrganizationPersonInRole> isInRole(RoleLabel role) {
    return pir -> role.equals(pir.getRole().getLabel());
  }

  private StudyForm cleanStudyFormNotificationStates(StudyForm studyForm, NotificationSubject subjectToBeCleaned) {
    List<RecipientNotificationState> cleanedStates = studyForm.getNotificationStates().stream()
      .filter(notificationState ->
        notificationState.getSubject().map(subject -> !subject.equals(subjectToBeCleaned)).orElse(false))
      .collect(Collectors.toList());

    studyForm.setNotificationStates(cleanedStates);
    return studyForm;
  }
}
