package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrganizationService implements Service<UUID, Organization> {

    private Service<NodeId, Node> nodeService;

    @Autowired
    public OrganizationService(Service<NodeId, Node> nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<Organization> query() {
        return nodeService.query("type.id:Organization").map(Organization::new);
    }

    @Override
    public Stream<Organization> query(String query) {
        return query();
    }

    @Override
    public Optional<Organization> get(UUID id) {
        return nodeService.get(new NodeId(id, "Organization")).map(Organization::new);
    }

    @Override
    public Organization save(Organization value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }

}
