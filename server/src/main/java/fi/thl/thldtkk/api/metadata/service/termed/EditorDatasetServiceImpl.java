package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;

public class EditorDatasetServiceImpl implements EditorDatasetService {

  private Repository<NodeId, Node> nodes;

  private UserHelper userHelper;

  public EditorDatasetServiceImpl(Repository<NodeId, Node> nodes, UserHelper userHelper) {
    this.nodes = nodes;
    this.userHelper = userHelper;
  }

  @Override
  public List<Dataset> findAll() {
    return find("", -1);
  }

  @Override
  public List<Dataset> find(String query, int max) {
    return find(null, null, query, max);
  }

  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max) {
    return find(organizationId, datasetTypeId, query, max, "");
  }

  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max, String sortString) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", Dataset.TERMED_NODE_CLASS));

    if(!userHelper.isCurrentUserAdmin()) {
      criteria.add(getCurrentUserOrganizationCriteria());
    }

    if (organizationId != null) {
      criteria.add(keyValue("references.owner.id", organizationId.toString()));
    }

    if (datasetTypeId != null) {
      criteria.add(keyValue("references.datasetType.id", datasetTypeId.toString()));
    }

    if (hasText(query)) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
    }

    if (hasText(sortString)) {
      return nodes.query(and(criteria), max, Sort.sort(sortString)).map(Dataset::new).collect(toList());
    }
    else {
      return nodes.query(and(criteria), max).map(Dataset::new).collect(toList());
    }
  }

  private Criteria getCurrentUserOrganizationCriteria() {
      List<String> organizationIds = userHelper.getCurrentUserOrganizations().stream()
        .map(organization -> organization.getId().toString())
        .collect(Collectors.toList());

      return keyWithAnyValue("references.owner.id", organizationIds);
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    Optional<Dataset> dataset = nodes.get(
      select(
        "id",
        "type",
        "lastModifiedDate",
        "properties.*",
        "references.*",
        "references.inScheme:2",
        "references.conceptsFromScheme:2",
        "references.variable:2",
        "references.quantity:2",
        "references.unit:2",
        "references.codeList:2",
        "references.source:2",
        "references.instanceQuestions:2",
        "references.personInRoles:2",
        "references.person:2",
        "references.role:2",
        "referrers.predecessors"),
      new NodeId(id, Dataset.TERMED_NODE_CLASS)).map(Dataset::new);

    if (dataset.isPresent()) {
      checkUserIsAllowedToAccessDataset(dataset.get());
    }

    return dataset;
  }

  @Override
  public Optional<Dataset> getDatasetWithAllInstanceVariableProperties(UUID datasetId) {
    Optional<Dataset> dataset = nodes.get(select(
      "id",
      "type",
      "lastModifiedDate",
      "properties.*",
      "references.*",
      "references.conceptsFromScheme:2",
      "references.variable:2",
      "references.quantity:2",
      "references.unit:2",
      "references.codeList:2",
      "references.source:2",
      "references.instanceQuestions:2",
      "references.personInRoles:2",
      "references.person:2",
      "references.role:2",
      "references.unitType:2",
      "references.inScheme:3",
      "references.codeItems:3"),
      new NodeId(datasetId, Dataset.TERMED_NODE_CLASS)).map(Dataset::new);

    if (dataset.isPresent()) {
      checkUserIsAllowedToAccessDataset(dataset.get());
    }

    return dataset;
  }

  private void checkUserIsAllowedToAccessDataset(Dataset dataset) {
    if (userHelper.isCurrentUserAdmin()) {
      // Admins can view and edit datasets of any organization
      return;
    }

    if (!dataset.getOwner().isPresent()) {
      throwDatasetAccessException(dataset, "which has no organization");
    }

    UUID datasetOrganizationId = dataset.getOwner().get().getId();
    Set<UUID> userOrganizationIds = userHelper.getCurrentUserOrganizations()
      .stream()
      .map(org -> org.getId())
      .collect(Collectors.toSet());

    if (!userOrganizationIds.contains(datasetOrganizationId)) {
      throwDatasetAccessException(dataset, "because user is not member of dataset's organization '" + datasetOrganizationId + "'");
    }
  }

  private void throwDatasetAccessException(Dataset dataset, String cause) {
    throw new AccessDeniedException(
      new StringBuilder()
        .append("User '")
        .append(userHelper.getCurrentUser().get().getUsername())
        .append("' is not allowed to view/save/delete dataset '")
        .append(dataset.getId())
        .append("' ")
        .append(cause)
        .toString()
    );
  }

  @Override
  public Dataset saveNotUpdatingInstanceVariables(Dataset dataset) {
    Optional<Dataset> optionalPrevDataset =
        dataset.getId() != null ? get(dataset.getId()) : Optional.empty();

    return save(optionalPrevDataset
        .map(prevDataset -> new Dataset(dataset, prevDataset.getInstanceVariables()))
        .orElse(dataset));
  }

  @Override
  public Dataset save(Dataset dataset) {
    Optional<Dataset> old;

    if (dataset.getId() == null) {
      old = empty();
      dataset.setId(randomUUID());
    } else {
      old = get(dataset.getId());
    }

    if (!old.isPresent()) {
      checkUserIsAllowedToAccessDataset(dataset);
    }

    if (containsSelf(dataset, dataset.getPredecessors())) {
      throw new IllegalArgumentException(
        new StringBuilder()
          .append("Cannot save dataset '")
          .append(dataset.getId())
          .append("' because it has a self reference in 'predecessors'")
          .toString());
    }

    boolean isDatasetPublished = dataset.isPublished().orElse(false);

    dataset.getPopulation()
        .ifPresent(p -> p.setId(firstNonNull(p.getId(), randomUUID())));
    dataset.getInstanceVariables()
        .forEach(iv -> {
          iv.setId(firstNonNull(iv.getId(), randomUUID()));
          iv.setPublished(isDatasetPublished);
          if (InstanceVariable.VALUE_DOMAIN_TYPE_DESCRIBED
              .equals(iv.getValueDomainType().orElse(null))) {
            iv.setCodeList(null);
          } else if (InstanceVariable.VALUE_DOMAIN_TYPE_ENUMERATED
              .equals(iv.getValueDomainType().orElse(null))) {
            iv.setQuantity(null);
            iv.setUnit(null);
            iv.setValueRangeMax(null);
            iv.setValueRangeMin(null);
          }
        });
    dataset.getLinks()
        .forEach(v -> v.setId(firstNonNull(v.getId(), randomUUID())));
    dataset.getPersonInRoles()
        .forEach(pir -> pir.setId(firstNonNull(pir.getId(), randomUUID())));

    dataset.setLastModifiedByUser(userHelper.getCurrentUser().get().getUserProfile());
    if (!old.isPresent()) {
      insert(dataset);
    } else {
      update(dataset, old.get());
    }

    return dataset;
  }

  private boolean containsSelf(Dataset dataset, List<Dataset> datasetReferences) {
    return datasetReferences
      .stream()
      .filter(d -> dataset.getId().equals(d.getId()))
      .findFirst()
      .isPresent();
  }

  private void insert(Dataset dataset) {
    List<Node> save = new ArrayList<>();

    save.add(dataset.toNode());
    dataset.getPopulation().ifPresent(v -> save.add(v.toNode()));
    dataset.getInstanceVariables().forEach(iv -> save.add(iv.toNode()));
    dataset.getLinks().forEach(v -> save.add(v.toNode()));
    dataset.getPersonInRoles().forEach(pir -> save.add(pir.toNode()));

    nodes.save(save);
  }

  private void update(Dataset newDataset, Dataset oldDataset) {
    Changeset<NodeId, Node> changeset = Changeset.<NodeId, Node>save(newDataset.toNode())
        .merge(buildChangeset(
            newDataset.getPopulation().orElse(null),
            oldDataset.getPopulation().orElse(null)))
        .merge(Changeset.buildChangeset(
            newDataset.getInstanceVariables(),
            oldDataset.getInstanceVariables()))
        .merge(Changeset.buildChangeset(
            newDataset.getLinks(),
            oldDataset.getLinks()))
        .merge(Changeset.buildChangeset(
            newDataset.getPersonInRoles(),
            oldDataset.getPersonInRoles()));

    nodes.post(changeset);
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

  @Override
  public InstanceVariable saveDatasetInstanceVariable(UUID datasetId,
      InstanceVariable instanceVariable) {

    Dataset dataset = get(datasetId).orElseThrow(NotFoundException::new);

    UUID instanceVariableId = firstNonNull(instanceVariable.getId(), randomUUID());
    instanceVariable.setId(instanceVariableId);

    Map<UUID, InstanceVariable> variablesById
        = index(dataset.getInstanceVariables(), InstanceVariable::getId);
    variablesById.put(instanceVariable.getId(), instanceVariable);

    dataset = save(new Dataset(dataset, new ArrayList<>(variablesById.values())));

    return dataset.getInstanceVariables().stream()
        .filter(iv -> instanceVariableId.equals(iv.getId()))
        .findFirst().orElseThrow(IllegalStateException::new);
  }

  @Override
  public void delete(UUID id) {
    Dataset dataset = get(id).orElseThrow(NotFoundException::new);

    List<Node> delete = new ArrayList<>();
    delete.add(dataset.toNode());

    // delete dependent nodes
    dataset.getPopulation().ifPresent(v -> delete.add(v.toNode()));
    dataset.getInstanceVariables().forEach(v -> delete.add(v.toNode()));
    dataset.getLinks().forEach(v -> delete.add(v.toNode()));
    dataset.getPersonInRoles().forEach(pir -> delete.add(pir.toNode()));

    nodes.delete(delete.stream().map(NodeId::new).collect(toList()));
  }

  @Override
  public void deleteDatasetInstanceVariable(UUID datasetId, UUID instanceVariableId) {
    Dataset dataset = get(datasetId).orElseThrow(NotFoundException::new);

    List<InstanceVariable> variables = new ArrayList<>();
    variables.addAll(dataset.getInstanceVariables());
    variables.removeIf(v -> v.getId().equals(instanceVariableId));

    save(new Dataset(dataset, variables));
  }

  @AdminOnly
  @Override
  public List<Dataset> getDatasetsByUnitType(UUID unitTypeId) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*"),
      and(keyValue("type.id", Dataset.TERMED_NODE_CLASS),
        keyValue("references.unitType.id", unitTypeId.toString())))
      .map(Dataset::new).collect(Collectors.toList());
  }

  @AdminOnly
  @Override
  public List<Dataset> getUniverseDatasets(UUID universeId){
    List<Dataset> list =  nodes.query(
            select("id", "type", "properties.*", "references.*", "referrers.*"),
            and(keyValue("type.id", Dataset.TERMED_NODE_CLASS),
                    keyValue("references.universe.id", universeId.toString())))
            .map(Dataset::new)
            .collect(toList());
    return list;
  }

}
