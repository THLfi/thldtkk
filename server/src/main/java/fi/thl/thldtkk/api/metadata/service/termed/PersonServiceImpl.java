package fi.thl.thldtkk.api.metadata.service.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.PersonService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PersonServiceImpl implements PersonService {

  private static final Logger LOG = LoggerFactory.getLogger(PersonServiceImpl.class);

  private Repository<NodeId, Node> nodes;

  public PersonServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Person> findAll() {
    return nodes.query(keyValue("type.id", "Person"))
        .map(Person::new)
        .collect(toList());
  }

  @Override
  public List<Person> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "Person"),
            anyKeyWithAllValues(
                asList("properties.firstName", "properties.lastName"),
                tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Person::new)
        .collect(toList());
  }

  @Override
  public Optional<Person> get(UUID id) {
    return nodes.get(new NodeId(id, "Person")).map(Person::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public Person save(@P("entity") Person person) {
    return new Person(nodes.save(person.toNode()));
  }

  public Person findPersonByFirstNameAndLastNameAndEmail(String firstName, String lastName, String email) {

    firstName = "\"" + firstName + "\"";
    lastName = "\"" + lastName + "\"";
    email = "\"" + email + "\"";

    List<Person> persons = nodes.query(
            and(KeyValueCriteria.keyValue(
                    "properties.firstName",
                    firstName),
                KeyValueCriteria.keyValue(
                    "properties.lastName",
                    lastName),
                KeyValueCriteria.keyValue(
                    "properties.email",
                    email)),
            1)
            .map(Person::new)
            .collect(toList());

    return persons != null && !persons.isEmpty() ? persons.get(0) : null;
  }
}
