package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

@Component
public class InstanceVariableService {

  private TermedNodeService nodeService;

  @Autowired
  public InstanceVariableService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  public Optional<InstanceVariable> get(UUID id) {
    return nodeService.get(new NodeId(id, "InstanceVariable"), "*,references.inScheme:2,references.codeItems:2,references.unitType:2").map(InstanceVariable::new);
  }

  public Stream<InstanceVariable> query(String query, int maxResults) {

      List<String> prefLabelClauses = constructSearchTerm(query, "InstanceVariable", "properties.prefLabel");
      List<String> descriptionClauses = constructSearchTerm(query, "InstanceVariable", "properties.description");
      List<String> technicalNameClauses = constructSearchTerm(query, "InstanceVariable", "properties.technicalName");
      
      StringBuffer assembledQuery = new StringBuffer();
      
      assembledQuery.append(String.join(" AND ", prefLabelClauses));
      assembledQuery.append(" OR (");
      assembledQuery.append(String.join(" AND ", descriptionClauses));
      assembledQuery.append(")");
      assembledQuery.append(" OR (");
      assembledQuery.append(String.join(" AND ", technicalNameClauses));
      assembledQuery.append(")");
          
    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(assembledQuery.toString())
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addIncludedAttribute("referrers.*")
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(InstanceVariable::new);
  }

  private List<String> constructSearchTerm(String query, String nodeType, String property) {
      
      List<String> propertySearchTermClauses = new ArrayList<String>();
      propertySearchTermClauses.add("type.id:" + nodeType);
      
      List<String> propertySearchTerms = stream(query.split("\\s"))
        .map(token -> property +":"+ token + "*")
        .collect(toList());
      
      propertySearchTermClauses.addAll(propertySearchTerms);
      return propertySearchTermClauses;
  }
  
}
