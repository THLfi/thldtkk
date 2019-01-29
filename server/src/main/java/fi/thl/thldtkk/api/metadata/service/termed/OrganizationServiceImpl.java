package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.service.OrganizationService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

  @AdminOnly
  @Override
  public Organization save(Organization organization) {
    if (organization.getId() == null) {
      return saveNewOrganization(organization);
    }

    // As we can only edit abbreviation and preferred label, check for existing organization entity
    // and merge changes. If that fails, make new organization with just that information
    try {
      return updateOldOrganization(organization);
    } catch (NotFoundException e) {
      return saveNewOrganization(organization);
    }
  }

  private Organization saveNewOrganization(Organization organization) {
    return new Organization(nodes.save(organization.toNode()));
  }

  private Organization updateOldOrganization(Organization organization) {
    Organization oldOrganization = new Organization(
      nodes.get(new NodeId(organization.getId(), "Organization"))
        .orElseThrow(NotFoundException::new));
    oldOrganization.setPrefLabel(organization.getPrefLabel());
    oldOrganization.setAbbreviation(organization.getAbbreviation());
    return new Organization(nodes.save(oldOrganization.toNode()));
  }

}
