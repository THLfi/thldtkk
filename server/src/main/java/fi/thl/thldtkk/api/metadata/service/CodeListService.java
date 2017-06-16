package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.CodeList;
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
public class CodeListService implements Service<UUID, CodeList> {

  private TermedNodeService nodeService;

  @Autowired
  public CodeListService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<CodeList> query() {
    return query("");
  }

  @Override
  public Stream<CodeList> query(String query) {
    return query(query, -1);
  }

  public Stream<CodeList> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());
    List<String> byOwner = stream(query.split("\\s"))
      .map(token -> "properties.owner:" + token + "*")
      .collect(toList());

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("type.id:CodeList AND ((");
    queryBuilder.append(String.join(" AND ", byPrefLabel));
    queryBuilder.append(") OR (");
    queryBuilder.append(String.join(" AND ", byOwner));
    queryBuilder.append("))");

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(queryBuilder.toString())
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(CodeList::new);
  }

  @Override
  public Optional<CodeList> get(UUID id) {
    return nodeService.get(new NodeId(id, "CodeList")).map(CodeList::new);
  }

  @Override
  public CodeList save(CodeList codeList) {
    Node savedNode = nodeService.save(codeList.toNode());
    return new CodeList(savedNode);
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
