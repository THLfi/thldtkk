package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.UsageCondition;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsageConditionService implements Service<UUID, UsageCondition> {

    private Service<NodeId, Node> nodeService;

    @Autowired
    public UsageConditionService(Service<NodeId, Node> nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<UsageCondition> query() {
        return nodeService.query("type.id:UsageCondition").map(UsageCondition::new);
    }

    @Override
    public Stream<UsageCondition> query(String query) {
        return query();
    }

    @Override
    public Optional<UsageCondition> get(UUID id) {
        return nodeService.get(new NodeId(id, "UsageCondition")).map(UsageCondition::new);
    }

    @Override
    public UsageCondition save(UsageCondition value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }

}
