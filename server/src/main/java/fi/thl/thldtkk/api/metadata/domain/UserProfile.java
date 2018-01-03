package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valuesToStringCollection;
import static java.util.Objects.requireNonNull;

public class UserProfile implements Serializable {

  public static final String TERMED_NODE_CLASS = "UserProfile";

  private UUID id;
  @NotBlank
  private String firstName;
  private String lastName;
  private String email;
  private Set<String> externalIds = new LinkedHashSet<>();
  private Set<String> roles = new LinkedHashSet<>();
  private Set<Organization> organizations = new LinkedHashSet<>();

  public UserProfile(){

  }

  public UserProfile(UUID id) {
    this.id = requireNonNull(id);
  }

  public UserProfile(UUID id,
                     String firstName,
                     String lastName,
                     String email,
                     Set<String> externalIds,
                     Set<String> roles,
                     Set<Organization> organizations) {
    this(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.externalIds = new LinkedHashSet<>(externalIds);
    this.roles = new LinkedHashSet<>(roles);
    this.organizations = new LinkedHashSet<>(organizations);
  }

  public UserProfile(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.firstName = PropertyMappings.toString(node.getProperties("firstName"));
    this.lastName = PropertyMappings.toString(node.getProperties("lastName"));
    this.email = PropertyMappings.toString(node.getProperties("email"));
    this.externalIds = valuesToStringCollection(node.getProperties("externalIds"), LinkedHashSet::new);
    this.roles = valuesToStringCollection(node.getProperties("roles"), LinkedHashSet::new);
    this.organizations = node.getReferences("organizations")
      .stream()
      .map(organizationNode -> new Organization(organizationNode))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public UUID getId() {
    return id;
  }

  public Optional<String> getFirstName() {
    return Optional.ofNullable(this.firstName);
  }

  public Optional<String> getLastName() {
    return Optional.ofNullable(this.lastName);
  }

  public Optional<String> getEmail() {
    return Optional.ofNullable(this.email);
  }

  public Set<String> getExternalIds() {
    return externalIds;
  }

  public Set<String> getRoles() {
    return roles;
  }

  public Set<Organization> getOrganizations() {
    return organizations;
  }

  public void setFirstName(String firstName){
    this.firstName = firstName;
  }

  public void setLastName(String lastName){
    this.lastName = lastName;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    getFirstName().ifPresent(fn -> props.put("firstName", toPropertyValue(fn)));
    getLastName().ifPresent(ln -> props.put("lastName", toPropertyValue(ln)));
    getEmail().ifPresent(e -> props.put("email", toPropertyValue(e)));
    props.putAll("externalIds", toPropertyValues(getExternalIds()));
    props.putAll("roles", toPropertyValues(getRoles()));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOrganizations().forEach(organization ->
      refs.put("organizations", organization.toNode()));

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
    UserProfile that = (UserProfile) o;
    return Objects.equals(id, that.id)
      && Objects.equals(firstName, that.firstName)
      && Objects.equals(lastName, that.lastName)
      && Objects.equals(email, that.email)
      && Objects.equals(externalIds, that.externalIds)
      && Objects.equals(roles, that.roles)
      && Objects.equals(organizations, that.organizations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, email, externalIds, roles, organizations);
  }

}
