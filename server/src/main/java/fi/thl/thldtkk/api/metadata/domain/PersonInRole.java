package fi.thl.thldtkk.api.metadata.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
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

public class PersonInRole implements NodeEntity {

  private static final String TERMED_NODE_CLASS = "PersonInRole";

  private UUID id;
  @JsonProperty("public")
  @SerializedName("public")
  private Boolean isPublic;
  @NotNull
  private Person person;
  private Role role;

  public PersonInRole(UUID id) {
    this.id = requireNonNull(id);
  }

  public PersonInRole(UUID id, Person person, Role role) {
    this.id = id;
    this.person = person;
    this.role = role;
  }

  public PersonInRole(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.isPublic = toBoolean(node.getProperties("public"), false);
    node.getReferencesFirst("person").ifPresent(p -> this.person = new Person(p));
    node.getReferencesFirst("role").ifPresent(p -> this.role = new Role(p));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<Boolean> isPublic() {
    return Optional.ofNullable(isPublic);
  }

  public Optional<Person> getPerson() {
    return Optional.ofNullable(this.person);
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Optional<Role> getRole() {
    return Optional.ofNullable(this.role);
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    isPublic().ifPresent(p -> props.put("public", toPropertyValue(p)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getPerson().ifPresent(p -> refs.put("person", p.toNode()));
    getRole().ifPresent(r -> refs.put("role", r.toNode()));

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
    PersonInRole that = (PersonInRole) o;
    return Objects.equals(id, that.id)
      && Objects.equals(person, that.person)
      && Objects.equals(role, that.role);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, person, role);
  }

}
