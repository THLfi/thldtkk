package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.NodeEntity;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.PublicStudyService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

public class PublicStudyServiceImpl implements PublicStudyService {

  private static final Logger LOG = LoggerFactory.getLogger(PublicStudyServiceImpl.class);

  private final Repository<NodeId, Node> nodes;
  private final UserHelper userHelper;

  public PublicStudyServiceImpl(Repository<NodeId, Node> nodes,
                                UserHelper userHelper) {
    this.nodes = nodes;
    this.userHelper = userHelper;
  }

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

    if (organizationId != null) {
      criteria.add(keyValue("references.ownerOrganization.id", organizationId.toString()));
    }

    if (hasText(query)) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
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
  public Optional<Study> get(UUID id) {
    Optional<Study> study = nodes.get(
      select(
        "id",
        "type",
        "lastModifiedDate",
        "properties.*",
        "references.*",
        "references.personInRoles:3",
        "references.person:4",
        "references.role:4",
        "references.links:3",
        "references.instanceVariable:3",
        "references.variable:3",
        "references.conceptsFromScheme:3",
        // Disabled because increases load times too much
        //"references.inScheme:4",
        "references.quantity:3",
        "references.unit:3",
        "references.codeList:3",
        "references.codeItems:4",
        "references.source:3",
        "references.unitType:3",
        "references.instanceQuestions:3",
        "references.usageCondition:2",
        "references.lifecyclePhase:2",
        "references.population:2",
        "references.universe:2",
        "references.datasetType:2",
        "references.ownerOrganizationUnit:2",
        "referrers.dataSets:2"),
      new NodeId(id, Study.TERMED_NODE_CLASS)).map(Study::new);

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
    Study savedStudy = saveStudyInternal(study, true, true);

    LOG.info("Saved study '{}'", savedStudy.getId());

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

    if (containsSelf(study, study.getPredecessors())) {
      throw new IllegalArgumentException(
        new StringBuilder()
          .append("Cannot save study '")
          .append(study.getId())
          .append("' because it has a self reference  in 'predecessors'")
          .toString());
    }

    boolean isStudyPublished = study.isPublished().orElse(false);

    study.getPopulation()
      .ifPresent(p -> p.setId(firstNonNull(p.getId(), randomUUID())));
    study.getLinks()
      .forEach(v -> v.setId(firstNonNull(v.getId(), randomUUID())));
    study.getPersonInRoles()
      .forEach(pir -> pir.setId(firstNonNull(pir.getId(), randomUUID())));

    if (includeDatasets) {
      study.getDatasets()
        .forEach(dataset -> {
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

  private <T extends NodeEntity> boolean containsSelf(T node, List<T> nodeRelations) {
    return nodeRelations != null ? nodeRelations.stream()
        .filter(s -> node.getId().equals(s.getId()))
        .findFirst()
        .isPresent()
      : false;
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
    Changeset<NodeId, Node> studyChangeset = Changeset.<NodeId, Node>empty()
      .merge(buildChangeset(
        newStudy.getPopulation().orElse(null),
        oldStudy.getPopulation().orElse(null)))
      .merge(Changeset.buildChangeset(
        newStudy.getLinks(),
        oldStudy.getLinks()))
      .merge(Changeset.buildChangeset(
        newStudy.getPersonInRoles(),
        oldStudy.getPersonInRoles()));

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

  // Deletion from public graph cannot be limited with @AdminOnly because
  // otherwise studies could not be withdrawn (and therefoe re-published)
  // by non-admin users
  @Override
  public void delete(UUID id) {
    Study study = get(id).orElseThrow(entityNotFound(Study.class, id));
    checkUserIsAllowedToAccessStudy(study);

    List<Node> delete = new ArrayList<>();

    delete.add(study.toNode());
    study.getPopulation().ifPresent(v -> delete.add(v.toNode()));
    study.getLinks().forEach(v -> delete.add(v.toNode()));
    study.getPersonInRoles().forEach(pir -> delete.add(pir.toNode()));
    study.getDatasets().forEach(dataset -> delete.addAll(getDatasetRelatedNodesForDelete(dataset)));

    nodes.delete(delete.stream().map(NodeId::new).collect(toList()));
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
  public List<Study> getStudiesByStudyGroup(UUID studyGroupId) {
    return nodes.query(
            select("id", "type", "properties.*", "references.*"),
            and(
                    keyValue("type.id", Study.TERMED_NODE_CLASS),
                    keyValue("references.studyGroup.id", studyGroupId.toString())))
            .map(Study::new)
            .collect(Collectors.toList());
  }

}
