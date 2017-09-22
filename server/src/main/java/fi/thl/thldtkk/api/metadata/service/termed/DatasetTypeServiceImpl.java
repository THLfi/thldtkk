package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.DatasetType;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.DatasetTypeService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DatasetTypeServiceImpl implements DatasetTypeService {

  private Repository<NodeId, Node> nodes;

  public DatasetTypeServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<DatasetType> findAll() {
    return nodes.query(
      keyValue("type.id", "DatasetType"),
      Sort.sort("properties.prefLabel.sortable"))
        .map(DatasetType::new)
        .collect(toList());
  }

  @Override
  public List<DatasetType> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<DatasetType> get(UUID id) {
    return nodes.get(new NodeId(id, "DatasetType")).map(DatasetType::new);
  }

}
