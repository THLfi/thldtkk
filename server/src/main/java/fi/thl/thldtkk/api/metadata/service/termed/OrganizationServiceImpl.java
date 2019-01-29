package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminAndOrgAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.method.P;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static java.util.stream.Collectors.toList;

public class OrganizationServiceImpl implements OrganizationService {

  private Repository<NodeId, Node> nodes;

  public OrganizationServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Organization> findAll() {
    return nodes.query(select("id",
      "type",
      "properties.*",
      "references.*",
      "referrers.*",
      "lastModifiedDate"), keyValue("type.id", "Organization"))
      .map(Organization::new)
      .collect(toList());
  }

  @Override
  public List<Organization> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<Organization> get(UUID id) {
    return nodes.get(select("id",
      "type",
      "properties.*",
      "references.*",
      "referrers.*",
      "lastModifiedDate"),new NodeId(id, "Organization")).map(Organization::new);
  }

  @Override
  public Optional<Organization> getByVirtuId(String virtuId) {
    return findAll()
      .stream()
      .filter(org -> org.getVirtuIds().contains(virtuId))
      .findFirst();
  }

  @UserCanCreateAdminAndOrgAdminCanUpdate
  @Override
  public Organization save(@P("entity") Organization organization) {
    //As we can only edit abbreviation and prefered label, check for existing organization entity
    //and merge changes. If that fails, make new organization with just that information
    try {
      Organization oldOrganization = new Organization(
        nodes.get(new NodeId(organization.getId(), "Organization")).orElseThrow(NotFoundException::new));

      oldOrganization.setAbbreviation(organization.getAbbreviation());
      oldOrganization.setPrefLabel(organization.getPrefLabel());

      return new Organization(nodes.save(oldOrganization.toNode()));

    } catch (NotFoundException e) {
      return new Organization(nodes.save(organization.toNode()));
    }

  }
}
