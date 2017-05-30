package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Concept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class ConceptService {
  private TermedNodeService nodeService;

  @Autowired
  public ConceptService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  public Stream<Concept> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:Concept");
    clauses.addAll(byPrefLabel);

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(Concept::new);
  }
}
