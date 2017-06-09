package fi.thl.thldtkk.api.metadata.service;

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
public class UnitService implements Service<UUID, Unit> {

  private TermedNodeService nodeService;

  @Autowired
  public UnitService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Unit> query() {
    return query("");
  }

  @Override
  public Stream<Unit> query(String query) {
    return query(query, -1);
  }

  public Stream<Unit> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:Unit");
    clauses.addAll(byPrefLabel);

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(Unit::new);
  }

  @Override
  public Optional<Unit> get(UUID id) {
    return nodeService.get(new NodeId(id, "Unit")).map(Unit::new);
  }

  @Override
  public Unit save(Unit unit) {
    Node savedNode = nodeService.save(unit.toNode());
    return new Unit(savedNode);
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
