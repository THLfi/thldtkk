package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Quantity;
import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class QuantityService implements Service<UUID, Quantity> {

  private TermedNodeService nodeService;

  @Autowired
  public QuantityService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Quantity> query() {
    return query("");
  }

  @Override
  public Stream<Quantity> query(String query) {
    return query(query, -1);
  }

  public Stream<Quantity> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:Quantity");
    clauses.addAll(byPrefLabel);

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(Quantity::new);
  }

  @Override
  public Optional<Quantity> get(UUID id) {
    return nodeService.get(new NodeId(id, "Quantity")).map(Quantity::new);
  }

  @Override
  public Quantity save(Quantity unit) {
    Node savedNode = nodeService.save(unit.toNode());
    return new Quantity(savedNode);
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
