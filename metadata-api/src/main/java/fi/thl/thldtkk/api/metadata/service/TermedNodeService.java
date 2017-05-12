package fi.thl.thldtkk.api.metadata.service;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Termed REST API backed service for Nodes.
 */
@Component
public class TermedNodeService implements Service<NodeId, Node> {

  private RestTemplate termed;

  private ParameterizedTypeReference<List<Node>> nodeListType =
    new ParameterizedTypeReference<List<Node>>() {
    };

  @Autowired
  public TermedNodeService(RestTemplate termed) {
    this.termed = termed;
  }

  @Override
  public Optional<Node> get(NodeId node) {
    return Optional.of(termed.getForObject("/types/{typeId}/node-trees/{id}?select=*", Node.class,
      node.getTypeId(), node.getId()));
  }

  @Override
  public Stream<Node> query() {
    return termed.exchange("/nodes?max=-1", GET, null, nodeListType).getBody().stream();
  }

  @Override
  public Stream<Node> query(String query) {
    return termed.exchange("/node-trees?select=id,type,properties.*&where={query}&max=-1", GET, null, nodeListType, query)
      .getBody().stream();
  }

  @Override
  public void save(List<Node> nodes) {
    termed.exchange("/nodes?batch=true", POST, new HttpEntity<>(nodes), nodeListType);
  }

  @Override
  public Node save(Node node) {
    return termed.postForObject("/nodes", node, Node.class);
  }

  @Override
  public void delete(NodeId node) {
    termed.delete("/types/{typeId}/nodes/{nodeId}", node.getTypeId(), node.getId());
  }

  @Override
  public void delete(List<NodeId> nodes) {
    termed.exchange("/nodes?batch=true", DELETE, new HttpEntity<>(nodes), Void.class);
  }

  @Override
  public void post(Changeset<NodeId, Node> changeset) {
    termed.exchange("/nodes?changeset=true", POST, new HttpEntity<>(changeset), Void.class);
  }

}
