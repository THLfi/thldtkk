package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganizationUnitService implements Service<UUID, OrganizationUnit> {

    private Service<NodeId, Node> nodeService;

    @Autowired
    public OrganizationUnitService(Service<NodeId, Node> nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<OrganizationUnit> query() {
        return nodeService.query("type.id:OrganizationUnit").map(OrganizationUnit::new);
    }

    @Override
    public Stream<OrganizationUnit> query(String query) {
        return query();
    }

    @Override
    public Optional<OrganizationUnit> get(UUID id) {
        return nodeService.get(new NodeId(id, "OrganizationUnit")).map(OrganizationUnit::new);
    }

    @Override
    public OrganizationUnit save(OrganizationUnit value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }

}
