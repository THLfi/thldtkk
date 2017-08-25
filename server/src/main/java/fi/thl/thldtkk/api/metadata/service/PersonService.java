package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

@Component
public class PersonService implements Service<UUID, Person> {

  private TermedNodeService nodeService;

  @Autowired
  public PersonService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<Person> query() {
    return query("");
  }

  @Override
  public Stream<Person> query(String query) {
    List<String> byLastName = stream(query.split("\\s"))
      .map(token -> "properties.lastName:" + token + "*")
      .collect(toList());
    List<String> byFirstName = stream(query.split("\\s"))
      .map(token -> "properties.firstName:" + token + "*")
      .collect(toList());

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("type.id:");
    queryBuilder.append(Person.TERMED_NODE_CLASS);
    queryBuilder.append(" AND ((");
    queryBuilder.append(String.join(" AND ", byLastName));
    queryBuilder.append(") OR (");
    queryBuilder.append(String.join(" AND ", byFirstName));
    queryBuilder.append("))");

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(queryBuilder.toString())
        .addSort("firstName")
        .build()
    ).map(Person::new);
  }

  @Override
  public Optional<Person> get(UUID id) {
    return nodeService.get(new NodeId(id, Person.TERMED_NODE_CLASS)).map(Person::new);
  }

  @Override
  public Person save(Person person) {
    return new Person(nodeService.save(person.toNode()));
  }

  @Override
  public void delete(UUID id) {
    throw new UnsupportedOperationException("Not implemented");
  }

}
