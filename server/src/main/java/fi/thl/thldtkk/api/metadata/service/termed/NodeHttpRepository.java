package fi.thl.thldtkk.api.metadata.service.termed;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Query;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.json.LocalDateTypeAdapter;
import fi.thl.thldtkk.api.metadata.util.json.MultimapTypeAdapterFactory;
import fi.thl.thldtkk.api.metadata.util.spring.GsonHttpMessageConverterFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.domain.query.Sort.sort;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * Termed REST API backend repository for Nodes.
 */
public class NodeHttpRepository implements Repository<NodeId, Node> {

  private static final Select DEFAULT_SELECT = select("id", "type", "properties.*", "references.*", "lastModifiedDate");
  private static final Sort DEFAULT_SORT = sort("properties.prefLabel.sortable");
  private static final int DEFAULT_MAX = -1;

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final RestTemplate termed;

  private ParameterizedTypeReference<List<Node>> nodeListType =
      new ParameterizedTypeReference<List<Node>>() {
      };

  public NodeHttpRepository(String apiUrl, UUID graphId, String username, String password) {
    Gson gson = new GsonBuilder()
      .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter().nullSafe())
      .registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
      .setPrettyPrinting()
      .create();

    this.termed = new RestTemplateBuilder()
      .rootUri(fromHttpUrl(apiUrl)
        .path("/graphs")
        .path("/" + graphId.toString())
        .toUriString())
      .messageConverters(GsonHttpMessageConverterFactory.build(gson))
      .basicAuthorization(username, password)
      .build();
  }

  @Override
  public Optional<Node> get(NodeId id) {
    return get(DEFAULT_SELECT, id);
  }

  @Override
  public Optional<Node> get(Select select, NodeId node) {
    try {
      return Optional.of(termed.getForObject("/types/{typeId}/node-trees/{id}?select={select}",
          Node.class, node.getTypeId(), node.getId(), select));
    } catch (HttpStatusCodeException e) {
      log.warn("{} {} {}", node.getTypeId(), node.getId(), e.getStatusCode());
      return Optional.empty();
    }
  }

  @Override
  public Stream<Node> query() {
    return termed.exchange("/node-trees?"
            + "select=" + DEFAULT_SELECT + "&"
            + "sort=" + DEFAULT_SORT + "&"
            + "max=" + DEFAULT_MAX, GET, null,
        nodeListType).getBody().stream();
  }

  @Override
  public Stream<Node> query(Criteria criteria) {
    return query(DEFAULT_SELECT, criteria, DEFAULT_SORT, DEFAULT_MAX);
  }

  @Override
  public Stream<Node> query(Criteria criteria, int max) {
    return query(DEFAULT_SELECT, criteria, DEFAULT_SORT, max);
  }

  @Override
  public Stream<Node> query(Select select, Criteria criteria) {
    return query(select, criteria, DEFAULT_SORT, DEFAULT_MAX);
  }

  @Override
  public Stream<Node> query(Select select, Criteria criteria, Sort sort) {
    return query(select, criteria, sort, DEFAULT_MAX);
  }

  @Override
  public Stream<Node> query(Select select, Criteria where, Sort sort, int max) {
    return termed.exchange("/node-trees?"
            + "select=" + select + "&"
            + "where=" + where + "&"
            + "sort=" + sort + "&"
            + "max=" + max, GET, null,
        nodeListType).getBody().stream();
  }

  @Override
  public Stream<Node> query(Query q) {
    return query(q.getSelect(), q.getWhere(), q.getSort(), q.getMax());
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
