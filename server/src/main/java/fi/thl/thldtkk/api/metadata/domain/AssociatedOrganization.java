package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static java.util.Objects.requireNonNull;

public class AssociatedOrganization implements NodeEntity {

  private static final String TERMED_NODE_CLASS = "AssociatedOrganization";

  private UUID id;
  private Boolean isRegistryOrganization;
  @NotNull
  private Organization organization;

  public AssociatedOrganization(UUID id) {
    this.id = requireNonNull(id);
  }

  /**
   * For testing purposes.
   */
  public AssociatedOrganization(UUID id, Organization organization, Boolean isRegistryOrganization) {
    this.id = id;
    this.organization = organization;
    this.isRegistryOrganization = isRegistryOrganization;
  }

  public AssociatedOrganization(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.isRegistryOrganization = toBoolean(node.getProperties("isRegistryOrganization"), false);
    node.getReferencesFirst("organization").ifPresent(o -> this.organization = new Organization(o));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<Boolean> isRegistryOrganization() {
    return Optional.ofNullable(isRegistryOrganization);
  }

  public Optional<Organization> getOrganization() {
    return Optional.ofNullable(this.organization);
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    isRegistryOrganization().ifPresent(val -> props.put("isRegistryOrganization", toPropertyValue(val)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOrganization().ifPresent(o -> refs.put("organization", o.toNode()));

    return new Node(id, TERMED_NODE_CLASS, props, refs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AssociatedOrganization that = (AssociatedOrganization) o;
    return Objects.equals(id, that.id)
      && Objects.equals(organization, that.organization);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, organization);
  }

}
