package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.domain.query.Sort.sort;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.UserProfile;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.UserProfileService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserProfileServiceImpl implements UserProfileService {

  private Repository<NodeId, Node> nodes;

  public UserProfileServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<UserProfile> findAll() {
    return nodes.query(
        select("id", "type", "properties.*", "references.*", "references.organizationUnit:2"),
        keyValue("type.id", "UserProfile"))
        .map(UserProfile::new)
        .collect(toList());
  }

  @Override
  public List<UserProfile> find(String query, int max) {
    Select select = select("id", "type",
        "properties.*",
        "references.*",
        "references.organizationUnit:2");

    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", "UserProfile")
        : and(
            keyValue("type.id", "UserProfile"),
            anyKeyWithAllValues(asList(
                "properties.firstName",
                "properties.lastName",
                "properties.email"),
                tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(select, criteria, sort("properties.firstName.sortable"), max)
        .map(UserProfile::new)
        .collect(toList());
  }

  @Override
  public Optional<UserProfile> get(UUID id) {
    return nodes.get(new NodeId(id, "UserProfile")).map(UserProfile::new);
  }

  @Override
  public Optional<UserProfile> getByExternalId(String externalId) {
    return findAll()
        .stream()
        .filter(userProfile -> userProfile.getExternalIds().contains(externalId))
        .findFirst();
  }

  @Override
  public UserProfile save(UserProfile userProfile) {
    return new UserProfile(nodes.save(userProfile.toNode()));
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
