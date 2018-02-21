package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Person;
import java.util.UUID;

public interface PersonService extends Service<UUID, Person> {

    Person findPersonByFirstNameAndLastNameAndEmail(String firstName, String lastName, String email);
}
