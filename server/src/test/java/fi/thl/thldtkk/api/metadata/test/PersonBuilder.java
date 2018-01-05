package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Person;

import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class PersonBuilder {

  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;

  public PersonBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public PersonBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public PersonBuilder withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public PersonBuilder withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public PersonBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public PersonBuilder withPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public Person build() {
    return new Person(id, firstName, lastName, email, phone);
  }

}
