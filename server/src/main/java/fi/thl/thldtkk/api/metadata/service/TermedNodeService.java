package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

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
    return get(node, "*");
  }

  @Override
  public Optional<Node> get(NodeId node, String select) {
    return Optional.of(termed.getForObject("/types/{typeId}/node-trees/{id}?select=" + select,
        Node.class, node.getTypeId(), node.getId()));
  }

  @Override
  public Stream<Node> query() {
    return termed.exchange("/nodes?max=-1", GET, null, nodeListType).getBody().stream();
  }

  @Override
  public Stream<Node> query(String query) {
    return query(
      new NodeRequestBuilder()
        .withQuery(query)
        .build());
  }

  public Stream<Node> query(NodeRequest request) {
    StringBuilder path = new StringBuilder("/node-trees?");
    Map<String, String> params = new LinkedHashMap<>();

    path.append("&select={includedAttributes}");
    params.put("includedAttributes", request.getIncludedAttributes());

    if (StringUtils.hasText(request.getQuery())) {
      path.append("&where={query}");
      params.put("query", request.getQuery());
    }

    if (StringUtils.hasText(request.getSort())) {
      path.append("&sort={sort}");
      params.put("sort", request.getSort());
    }

    path.append("&max={maxResults}");
    params.put("maxResults", Integer.toString(request.getMaxResults()));

    return termed.exchange(path.toString(), GET, null, nodeListType, params).getBody().stream();
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
