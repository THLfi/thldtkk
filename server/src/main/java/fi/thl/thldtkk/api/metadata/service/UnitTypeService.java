package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UnitTypeService implements Service<UUID, UnitType> {

    private TermedNodeService nodeService;

    @Autowired
    public UnitTypeService(TermedNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Stream<UnitType> query() {
        return query("");
    }

    @Override
    public Stream<UnitType> query(String query) {
        return query(query, -1);
    }

    public Stream<UnitType> query(String query, int maxResults) {
        List<String> byPrefLabel = stream(query.split("\\s"))
                .map(token -> "properties.prefLabel:" + token + "*")
                .collect(toList());
        List<String> byOwner = stream(query.split("\\s"))
                .map(token -> "properties.owner:" + token + "*")
                .collect(toList());

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("type.id:UnitType AND ((");
        queryBuilder.append(String.join(" AND ", byPrefLabel));
        queryBuilder.append(") OR (");
        queryBuilder.append(String.join(" AND ", byOwner));
        queryBuilder.append("))");

        return nodeService.query(
                new NodeRequestBuilder()
                .withQuery(queryBuilder.toString())
                .addDefaultIncludedAttributes()
                .addIncludedAttribute("references.*")
                .addSort("prefLabel")
                .withMaxResults(maxResults)
                .build()
        ).map(UnitType::new);
    }

    @Override
    public Optional<UnitType> get(UUID id) {
        return nodeService.get(new NodeId(id, "UnitType")).map(UnitType::new);
    }

    @Override
    public UnitType save(UnitType value) {
        Node savedNode = nodeService.save(value.toNode());
        return new UnitType(savedNode);
    }

}
