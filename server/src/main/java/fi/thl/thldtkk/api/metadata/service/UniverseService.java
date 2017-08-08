package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Universe;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class UniverseService implements Service<UUID, Universe> {

    private TermedNodeService nodeService;

    @Autowired
    public UniverseService(TermedNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Stream<Universe> query() {
        return query("");
    }

    @Override
    public Stream<Universe> query(String query) {
        return query(query, -1);
    }

    public Stream<Universe> query(String query, int maxResults) {
        List<String> byPrefLabel = stream(query.split("\\s"))
                .map(token -> "properties.prefLabel:" + token + "*")
                .collect(toList());

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("type.id:Universe AND ");
        queryBuilder.append(String.join(" AND ", byPrefLabel));

        return nodeService.query(
                new NodeRequestBuilder()
                .withQuery(queryBuilder.toString())
                .addDefaultIncludedAttributes()
                .addIncludedAttribute("references.*")
                .addSort("prefLabel")
                .withMaxResults(maxResults)
                .build()
        ).map(Universe::new);
    }

    @Override
    public Optional<Universe> get(UUID id) {
        return nodeService.get(new NodeId(id, "Universe")).map(Universe::new);
    }

    @Override
    public Universe save(Universe value) {
        Node savedNode = nodeService.save(value.toNode());
        return new Universe(savedNode);
    }

}
