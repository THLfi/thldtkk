package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminAndOrgAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.OrganizationUnitService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.security.access.method.P;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

public class OrganizationUnitServiceImpl implements OrganizationUnitService {

  private Repository<NodeId, Node> nodes;

  public OrganizationUnitServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<OrganizationUnit> findAll() {
    return nodes.query(select("id",
                              "type",
                              "properties.*",
                              "references.*",
                              "references.person:2",
                              "references.role:2",
                              "referrers.*",
                              "lastModifiedDate"), keyValue("type.id", "OrganizationUnit"))
        .map(OrganizationUnit::new)
        .collect(toList());
  }

  @Override
  public List<OrganizationUnit> find(String query, int max) {
    return findAll();
  }

  @Override
  public Optional<OrganizationUnit> get(UUID id) {
    return nodes.get(select("id",
                            "type",
                            "properties.*",
                            "references.*",
                            "references.person:2",
                            "references.role:2",
                            "referrers.*",
                            "referrers.organization:2",
                            "lastModifiedDate") , new NodeId(id, "OrganizationUnit")).map(OrganizationUnit::new);
  }

  @Override
  public Optional<OrganizationUnit> findByAbbreviation(String abbreviation) {
    if (abbreviation == null || abbreviation.isEmpty()) {
      return Optional.empty();
    }

    abbreviation = "\"" + abbreviation + "\"";

    return nodes.query(
            KeyValueCriteria.keyValue(
                    "properties.abbreviation",
                    abbreviation),
            1)
            .map(OrganizationUnit::new)
            .findFirst();
  }

  @UserCanCreateAdminAndOrgAdminCanUpdate
  @Override
  public OrganizationUnit save(OrganizationUnit organizationUnit) {
    return new OrganizationUnit(nodes.save(organizationUnit.toNode()));
  }

  @UserCanCreateAdminAndOrgAdminCanUpdate
  @Override
  public OrganizationUnit save(UUID organizationId, @P("entity") OrganizationUnit organizationUnit) {
    Organization organization = new Organization(
      nodes.get(new NodeId(organizationId, "Organization")).orElseThrow(NotFoundException::new));

    organization.addOrganizationUnit(organizationUnit);

    OrganizationUnit old = null;
    if (organizationUnit.getId() != null) {
      old = get(organization.getId()).orElse(null);
    } else {
      organizationUnit.setId(UUID.randomUUID());
    }

    Changeset<NodeId, Node> changeset = saveForPersonInRoles(organizationUnit, old);
    changeset = changeset.merge(new Changeset<>(
      Collections.emptyList(),
      asList(organization.toNode(), organizationUnit.toNode())
    ));

    nodes.post(changeset);

    return organizationUnit;
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    OrganizationUnit organizationUnit = get(id).orElseThrow(NotFoundException::new);

    UUID parentOrganizationId = organizationUnit.getParentOrganizationId();

    Organization organization = new Organization(
    nodes.get(new NodeId(parentOrganizationId, "Organization")).orElseThrow(NotFoundException::new));
    organization.removeOrganizationUnit(organizationUnit);

    nodes.save(organization.toNode());
    nodes.delete(new NodeId(id, "OrganizationUnit"), true);
  }

  private Changeset<NodeId, Node> saveForPersonInRoles(OrganizationUnit unit, OrganizationUnit old) {
    unit.getPersonInRoles().stream()
      .filter(personInRole -> personInRole.getId() == null)
      .forEach(personInRole -> personInRole.setId(UUID.randomUUID()));

    return Changeset.buildChangeset(
      unit.getPersonInRoles(),
      old != null ? old.getPersonInRoles() : Collections.emptyList()
    );
  }

}
