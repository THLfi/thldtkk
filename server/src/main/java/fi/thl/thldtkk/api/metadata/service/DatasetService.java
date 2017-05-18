package fi.thl.thldtkk.api.metadata.service;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Population;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Dataset service that converts Datasets to nodes and back, and delegates actual CRUD operations to
 * given node service.
 */
@Component
public class DatasetService implements Service<UUID, Dataset> {

  private Service<NodeId, Node> nodeService;

  @Autowired
  public DatasetService(Service<NodeId, Node> nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Dataset> query() {
    return nodeService.query("type.id:DataSet").map(Dataset::new);
  }

  @Override
  public Stream<Dataset> query(String query) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:DataSet");
    clauses.addAll(byPrefLabel);

    return nodeService.query(String.join(" AND ", clauses)).map(Dataset::new);
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    return nodeService.get(new NodeId(id, "DataSet")).map(Dataset::new);
  }

  @Override
  public Dataset save(Dataset dataset) {
    Optional<Dataset> old = dataset.getId() != null ? get(dataset.getId()) : empty();

    dataset.getInstanceVariables().stream()
      .filter(instanceVariable -> instanceVariable.getId() == null)
      .forEach(instanceVariable -> instanceVariable.setId(UUID.randomUUID()));

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
    dataset.getInstanceVariables().forEach(v -> save.add(v.toNode()));

    nodeService.save(save);
  }

  private void update(Dataset newDataset, Dataset oldDataset) {
    Changeset<NodeId, Node> changeset = Changeset.<NodeId, Node>save(newDataset.toNode())
      .merge(buildChangeset(
        newDataset.getPopulation().orElse(null),
        oldDataset.getPopulation().orElse(null)))
      .merge(buildChangeset(
        newDataset.getInstanceVariables(),
        oldDataset.getInstanceVariables()));

    nodeService.post(changeset);
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
        newPopulation.getDescription(),
        newPopulation.getSampleSize(),
        newPopulation.getLoss(),
        newPopulation.getGeographicalCoverage()).toNode());
    }
    if (newPopulation == null && oldPopulation != null) {
      return Changeset.delete(new NodeId(oldPopulation.toNode()));
    }
    return Changeset.empty();
  }

  private Changeset<NodeId, Node> buildChangeset(
    List<InstanceVariable> newInstanceVariables,
    List<InstanceVariable> oldInstanceVariables) {

    Map<UUID, InstanceVariable> newVarsById = newInstanceVariables.stream()
      .collect(toMap(InstanceVariable::getId, Function.identity()));
    Map<UUID, InstanceVariable> oldVarsById = oldInstanceVariables.stream()
      .collect(toMap(InstanceVariable::getId, Function.identity()));

    MapDifference<UUID, InstanceVariable> diff = Maps.difference(newVarsById, oldVarsById);

    List<Node> save = new ArrayList<>();

    save.addAll(diff.entriesOnlyOnLeft().values().stream()
      .map(InstanceVariable::toNode)
      .collect(toList()));
    save.addAll(diff.entriesDiffering().values().stream()
      .map(ValueDifference::leftValue)
      .map(InstanceVariable::toNode)
      .collect(toList()));

    List<NodeId> delete = diff.entriesOnlyOnRight().values().stream()
      .map(InstanceVariable::toNode)
      .map(NodeId::new)
      .collect(toList());

    return new Changeset<>(delete, save);
  }

  @Override
  public void delete(UUID id) {
    Dataset dataset = get(id).orElseThrow(NotFoundException::new);

    List<Node> delete = new ArrayList<>();
    delete.add(dataset.toNode());

    // delete dependent nodes
    dataset.getPopulation().ifPresent(v -> delete.add(v.toNode()));
    dataset.getInstanceVariables().forEach(v -> delete.add(v.toNode()));

    nodeService.delete(delete.stream().map(NodeId::new).collect(toList()));
  }

}
