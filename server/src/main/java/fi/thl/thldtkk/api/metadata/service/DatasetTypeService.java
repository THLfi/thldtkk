package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.DatasetType;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetTypeService implements Service<UUID, DatasetType> {

  private Service<NodeId, Node> nodeService;

  @Autowired
  public DatasetTypeService(Service<NodeId, Node> nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<DatasetType> query() {
    return nodeService.query("type.id:DatasetType").map(DatasetType::new);
  }

  @Override
  public Stream<DatasetType> query(String query) {
    return query();
  }

  @Override
  public Optional<DatasetType> get(UUID id) {
    return nodeService.get(new NodeId(id, "DatasetType")).map(DatasetType::new);
  }

  @Override
  public DatasetType save(DatasetType value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException();
  }
}
