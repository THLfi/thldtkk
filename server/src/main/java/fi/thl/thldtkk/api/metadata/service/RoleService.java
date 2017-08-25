package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Role;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class RoleService implements Service<UUID, Role> {

  private TermedNodeService nodeService;

  @Autowired
  public RoleService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Role> query() {
    return query("");
  }

  @Override
  public Stream<Role> query(String query) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());

    List<String> clauses = new ArrayList<>();
    clauses.add("type.id:" + Role.TERMED_NODE_CLASS);
    clauses.addAll(byPrefLabel);

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(String.join(" AND ", clauses))
        .addSort("prefLabel")
        .build()
    ).map(Role::new);
  }

  @Override
  public Optional<Role> get(UUID id) {
    return nodeService.get(new NodeId(id, Role.TERMED_NODE_CLASS)).map(Role::new);
  }

  @Override
  public Role save(Role role) {
    return new Role(nodeService.save(role.toNode()));
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
