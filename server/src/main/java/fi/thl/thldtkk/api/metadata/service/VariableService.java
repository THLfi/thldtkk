package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Variable;
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
public class VariableService implements Service<UUID, Variable> {
    
    private TermedNodeService nodeService;
    

    @Autowired
    public VariableService(TermedNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<Variable> query() {
        return nodeService.query("type.id:Variable").map(Variable::new);
    }

    @Override
    public Stream<Variable> query(String query) {
        return query();
    }

    @Override
    public Optional<Variable> get(UUID id) {
        return nodeService.get(new NodeId(id, "Variable")).map(Variable::new);
    }

    @Override
    public Variable save(Variable value) {
        Node saved = nodeService.save(value.toNode());
        return new Variable(saved);
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }
    
    public Stream<Variable> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:Variable");
    clauses.addAll(byPrefLabel);

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addDefaultIncludedAttributes()
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(Variable::new);
  }
}
