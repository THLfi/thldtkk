package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class UserProfileService implements Service<UUID, UserProfile> {

  private TermedNodeService nodeService;

  @Autowired
  public UserProfileService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<UserProfile> query() {
    return query("");
  }

  @Override
  public Stream<UserProfile> query(String query) {
    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("type.id:");
    queryBuilder.append(UserProfile.TERMED_NODE_CLASS);
    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(queryBuilder.toString())
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addSort("firstName")
        .build()
    ).map(UserProfile::new);
  }

  @Override
  public Optional<UserProfile> get(UUID id) {
    return nodeService.get(new NodeId(id, UserProfile.TERMED_NODE_CLASS))
      .map(UserProfile::new);
  }

  @Override
  public UserProfile save(UserProfile userProfile) {
    return new UserProfile(nodeService.save(userProfile.toNode()));
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
