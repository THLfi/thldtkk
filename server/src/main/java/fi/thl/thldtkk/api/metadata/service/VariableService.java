package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;

public class VariableService implements Service<UUID, Variable> {

    private Service<NodeId, Node> nodeService;

    @Autowired
    public VariableService(Service<NodeId, Node> nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Stream<Variable> query() {
        return nodeService.query("type.id:Variable").map(Variable::new);
    }

    @Override
    public Stream<Variable> query(String query) {
        return query();
    }

    @Override
    public Optional<Variable> get(UUID id) {
        return nodeService.get(new NodeId(id, "Variable")).map(Variable::new);
    }

    @Override
    public Variable save(Variable value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(UUID id) {
        throw new UnsupportedOperationException();
    }
}
