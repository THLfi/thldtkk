package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Stream<InstanceVariable> extendedQuery(String query) {
        return extendedQuery(query, -1);
    }

    public Stream<InstanceVariable> extendedQuery(String query, int maxResults) {

        List<String> prefLabelClauses = constructSearchTerm(query, "properties.prefLabel");
        List<String> descriptionClauses = constructSearchTerm(query, "properties.description");
        List<String> technicalNameClauses = constructSearchTerm(query, "properties.technicalName");
        List<String> freeConceptClauses = constructSearchTerm(query, "properties.freeConcepts");

        List<String> conceptsFromSchemeClauses = constructSearchTerm(query, "references.conceptsFromScheme.properties.prefLabel");
        List<String> variablePrefLabelClauses = constructSearchTerm(query, "references.variable.properties.prefLabel");

        StringBuilder assembledQuery = new StringBuilder();

        assembledQuery.append("type.id:InstanceVariable");
        assembledQuery.append(" AND (");
        assembledQuery.append("(");
        assembledQuery.append(String.join(" AND ", prefLabelClauses));
        assembledQuery.append(")");

        assembledQuery = joinQueryClauses(assembledQuery, descriptionClauses,
                technicalNameClauses, freeConceptClauses, conceptsFromSchemeClauses,
                variablePrefLabelClauses);

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

    private StringBuilder joinQueryClauses(StringBuilder querySegment, List<String>... clauses) {
        for (List<String> clause : clauses) {
            querySegment.append(" OR (");
            querySegment.append(String.join(" AND ", clause));
            querySegment.append(")");
        }
        return querySegment;
    }

    public Stream<InstanceVariable> query(String query, int maxResults) {

        List<String> prefLabelClauses = constructSearchTerm(query, "properties.prefLabel");
        List<String> descriptionClauses = constructSearchTerm(query, "properties.description");
        List<String> technicalNameClauses = constructSearchTerm(query, "properties.technicalName");
        List<String> freeConceptClauses = constructSearchTerm(query, "properties.freeConcepts");

        StringBuilder assembledQuery = new StringBuilder();

        assembledQuery.append("type.id:InstanceVariable");
        assembledQuery.append(" AND (");

        assembledQuery.append(String.join(" AND ", prefLabelClauses));
        assembledQuery.append(" OR (");
        assembledQuery.append(String.join(" AND ", descriptionClauses));
        assembledQuery.append(")");
        assembledQuery.append(" OR (");
        assembledQuery.append(String.join(" AND ", technicalNameClauses));
        assembledQuery.append(")");
        assembledQuery.append(" OR (");
        assembledQuery.append(String.join(" AND ", freeConceptClauses));
        assembledQuery.append(")");

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

    private List<String> constructSearchTerm(String query, String property) {

        List<String> propertySearchTermClauses = new ArrayList<>();

        List<String> propertySearchTerms = stream(query.split("\\s"))
                .map(token -> property + ":" + token + "*")
                .collect(toList());

        propertySearchTermClauses.addAll(propertySearchTerms);
        return propertySearchTermClauses;
    }

    public List<InstanceVariable> getInstanceVariablesByVariableId(UUID variableId, int maxResults) {
        String query = "type.id:InstanceVariable AND refs.variable.id:" + variableId;
        return nodeService.query(
                new NodeRequestBuilder()
                .withQuery(query)
                .addDefaultIncludedAttributes()
                .addIncludedAttribute("references.*")
                .addIncludedAttribute("referrers.*")
                .addSort("prefLabel")
                .withMaxResults(maxResults)
                .build()
        ).map(InstanceVariable::new).collect(Collectors.toList());
    }

}
