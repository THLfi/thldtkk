package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static java.util.Objects.requireNonNull;

public class Person {

  public static final String TERMED_NODE_CLASS = "Person";

  private UUID id;
  @NotBlank
  private String firstName;
  private String lastName;
  private String email;
  private String phone;

  public Person(UUID id) {
    this.id = requireNonNull(id);
  }

  public Person(UUID id, String firstName, String lastName, String email, String phone) {
    this(id);
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
  }

  public Person(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.firstName = PropertyMappings.toString(node.getProperties("firstName"));
    this.lastName = PropertyMappings.toString(node.getProperties("lastName"));
    this.email = PropertyMappings.toString(node.getProperties("email"));
    this.phone = PropertyMappings.toString(node.getProperties("phone"));
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
  public Optional<String> getPhone() {
    return Optional.ofNullable(this.phone);
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    getFirstName().ifPresent(fn -> props.put("firstName", toPropertyValue(fn)));
    getLastName().ifPresent(ln -> props.put("lastName", toPropertyValue(ln)));
    getEmail().ifPresent(e -> props.put("email", toPropertyValue(e)));
    getPhone().ifPresent(p -> props.put("phone", toPropertyValue(p)));
    return new Node(id, TERMED_NODE_CLASS, props);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person that = (Person) o;
    return Objects.equals(id, that.id)
      && Objects.equals(firstName, that.firstName)
      && Objects.equals(lastName, that.lastName)
      && Objects.equals(email, that.email)
      && Objects.equals(phone, that.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, email, phone);
  }

}
