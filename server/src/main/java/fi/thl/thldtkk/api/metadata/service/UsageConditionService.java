package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.UsageCondition;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsageConditionService implements Service<UUID, UsageCondition> {

    private TermedNodeService nodeService;

    @Autowired
    public UsageConditionService(TermedNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<UsageCondition> query() {
      NodeRequestBuilder builder = new NodeRequestBuilder();
      builder.addDefaultIncludedAttributes();
      builder.withQuery("type.id:UsageCondition");
      builder.addSort("prefLabel");
      return nodeService.query(builder.build()).map(UsageCondition::new);
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
