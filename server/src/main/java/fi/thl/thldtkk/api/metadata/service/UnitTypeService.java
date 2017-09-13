package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class UnitTypeService implements Service<UUID, UnitType> {

    private TermedNodeService nodeService;

    @Autowired
    public UnitTypeService(TermedNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void delete(UUID id) {
        UnitType unitType = get(id).orElseThrow(NotFoundException::new);       
        nodeService.delete(new NodeId(unitType.toNode()));
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

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("type.id:UnitType AND ");
        queryBuilder.append(String.join(" AND ", byPrefLabel));

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
