package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;


public class PublicDatasetServiceImpl implements PublicDatasetService {

  private Repository<NodeId, Node> nodes;

  public PublicDatasetServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    return nodes.get(select("id", "type", "properties.*", "references.*",
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
        "referrers.predecessor"),
        new NodeId(id, Dataset.TERMED_NODE_CLASS)).map(Dataset::new);
  }

  @Override
  public Optional<Dataset> getDatasetWithAllInstanceVariableProperties(UUID datasetId) {
    Optional<Dataset> dataset = nodes.get(select("id", "type", "properties.*", "references.*",
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

    return dataset;
  }


  @Override
  public List<Dataset> findAll() {
    return nodes.query(keyValue("type.id", Dataset.TERMED_NODE_CLASS))
        .map(Dataset::new)
        .collect(toList());
  }

  @Override
  public List<Dataset> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", Dataset.TERMED_NODE_CLASS),
            keyWithAnyValue("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Dataset::new)
        .collect(toList());
  }

  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", Dataset.TERMED_NODE_CLASS));

    if (organizationId != null) {
      criteria.add(keyValue("references.organization.id", organizationId.toString()));
    }
    if (datasetTypeId != null) {
      criteria.add(keyValue("references.datasetType.id", datasetTypeId.toString()));
    }
    if (!query.isEmpty()) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
    }

    return nodes.query(and(criteria), max).map(Dataset::new).collect(toList());
  }

  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max, String sortString) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", Dataset.TERMED_NODE_CLASS));

    if (organizationId != null) {
      criteria.add(keyValue("references.owner.id", organizationId.toString()));
    }
    if (datasetTypeId != null) {
      criteria.add(keyValue("references.datasetType.id", datasetTypeId.toString()));
    }
    if (!query.isEmpty()) {
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

  @Override
  public Dataset save(Dataset dataset) {
    Optional<Dataset> old;

    if (dataset.getId() == null) {
      old = empty();
      dataset.setId(randomUUID());
    } else {
      old = get(dataset.getId());
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

    if (!old.isPresent()) {
      insert(dataset);
    } else {
      update(dataset, old.get());
    }

    return dataset;
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

}
