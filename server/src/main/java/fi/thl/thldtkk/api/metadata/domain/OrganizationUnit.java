package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class OrganizationUnit implements NodeEntity, Serializable {

  private UUID id;
  private UUID parentOrganizationId;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> abbreviation = new LinkedHashMap<>();
  private List<OrganizationPersonInRole> personInRoles = new ArrayList<>();

  /**
   * Required by GSON deserialization.
   */
  private OrganizationUnit() {

  }

  public OrganizationUnit(UUID id) {
    this.id = requireNonNull(id);
  }

  public OrganizationUnit(UUID id,
                          UUID parentOrganizationId,
                          Map<String, String> prefLabel,
                          Map<String, String> abbreviation) {

    this(id);
    this.parentOrganizationId = parentOrganizationId;
    this.prefLabel = prefLabel;
    this.abbreviation = abbreviation;
  }

  public OrganizationUnit(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "OrganizationUnit"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.abbreviation = toLangValueMap(node.getProperties("abbreviation"));

    try {
      this.parentOrganizationId = new Organization(node.getReferrersFirst("organizationUnit").orElseThrow(NotFoundException::new)).getId();
    } catch (Exception e) {
      this.parentOrganizationId = null;
    }

    this.personInRoles = node.getReferences("personInRoles").stream()
      .map(OrganizationPersonInRole::new)
      .collect(Collectors.toList());

  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getParentOrganizationId() {
    return parentOrganizationId;
  }
  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }
  public Map<String, String> getAbbreviation() {
    return abbreviation;
  }

  public void setPersonInRoles(List<OrganizationPersonInRole> personInRoles) {
    this.personInRoles = personInRoles;
  }

  public List<OrganizationPersonInRole> getPersonInRoles() {
    return personInRoles;
  }


  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("abbreviation", toPropertyValues(abbreviation));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getPersonInRoles().forEach(personInRole -> refs.put("personInRoles", personInRole.toNode()));

    return new Node(id, "OrganizationUnit", props, refs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationUnit that = (OrganizationUnit) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(prefLabel, that.prefLabel) &&
      Objects.equals(personInRoles, that.personInRoles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, personInRoles);
  }
}
