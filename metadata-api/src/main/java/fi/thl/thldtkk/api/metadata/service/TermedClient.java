package fi.thl.thldtkk.api.metadata.service;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TermedClient {

  private RestTemplate termed;

  private ParameterizedTypeReference<List<Node>> nodeListType =
    new ParameterizedTypeReference<List<Node>>() {
    };

  @Autowired
  public TermedClient(RestTemplate termed) {
    this.termed = termed;
  }

  public Optional<Node> get(String typeId, UUID id) {
    return Optional.of(termed.getForObject("/types/{typeId}/nodes/{id}", Node.class, typeId, id));
  }

  /**
   * Uses node-tree api to get more complex nodes.
   */
  public Optional<Node> getTree(String typeId, UUID id, String select) {
    return Optional.of(termed.getForObject("/types/{typeId}/node-trees/{id}?select={select}",
      Node.class, typeId, id, select));
  }

  public List<Node> query(String typeId) {
    return termed.exchange("/types/{typeId}/nodes", GET, null, nodeListType, typeId).getBody();
  }

  public Node save(String typeId, Node node) {
    return termed.postForObject("/types/{typeId}/nodes", node, Node.class, typeId);
  }

  public void save(List<Node> nodes) {
    termed.exchange("/nodes?batch=true", POST, new HttpEntity<>(nodes), nodeListType);
  }

  public void save(String typeId, List<Node> nodes) {
    termed.exchange("/types/{typeId}/nodes?batch=true", POST, new HttpEntity<>(nodes),
      nodeListType, typeId);
  }

  public void delete(String typeId, UUID id) {
    termed.delete("/types/{typeId}/nodes/{nodeId}", typeId, id);
  }

  public void delete(List<Node> nodes) {
    termed.exchange("/nodes?batch=true", DELETE, new HttpEntity<>(nodes), nodeListType);
  }

}
