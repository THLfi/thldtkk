package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InstanceQuestionService implements Service<UUID, InstanceQuestion> {

  private TermedNodeService nodeService;

  @Autowired
  public InstanceQuestionService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }
  
  @Override
  public Stream<InstanceQuestion> query() {
    return query("");
  }

  @Override
  public Stream<InstanceQuestion> query(String query) {
    return query(query, -1);
  }

  public Stream<InstanceQuestion> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:"+InstanceQuestion.TERMED_NODE_CLASS);
    clauses.addAll(byPrefLabel);
    
    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(InstanceQuestion::new);
  }
  
  
  @Override
  public Optional<InstanceQuestion> get(UUID id) {
    return nodeService.get(new NodeId(id, InstanceQuestion.TERMED_NODE_CLASS)).map(InstanceQuestion::new);
  }

  @Override
  public InstanceQuestion save(InstanceQuestion instanceQuestion) {
        Node savedNode = nodeService.save(instanceQuestion.toNode());
    return new InstanceQuestion(savedNode);
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
