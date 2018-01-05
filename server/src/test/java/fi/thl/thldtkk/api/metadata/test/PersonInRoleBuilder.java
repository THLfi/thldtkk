package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.domain.Role;

import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class PersonInRoleBuilder {

  private UUID id;
  private Person person;
  private Role role;

  public PersonInRoleBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public PersonInRoleBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public PersonInRoleBuilder withPerson(Person person) {
    this.person = person;
    return this;
  }

  public PersonInRole build() {
    return new PersonInRole(id, person, role);
  }

}
