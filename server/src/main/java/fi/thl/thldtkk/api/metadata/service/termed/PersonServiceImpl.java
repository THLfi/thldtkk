package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.EditorPersonInRoleService;
import fi.thl.thldtkk.api.metadata.service.PersonService;
import fi.thl.thldtkk.api.metadata.service.PublicPersonInRoleService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class PersonServiceImpl implements PersonService {

  private Repository<NodeId, Node> nodes;

  @Autowired
  private EditorPersonInRoleService editorPersonInRoleService;

  @Autowired
  private PublicPersonInRoleService publicPersonInRoleService;

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
    Criteria criteria = query.isEmpty()
        ? keyValue("type.id", "Person")
        : and(
            keyValue("type.id", "Person"),
            anyKeyWithAllValues(
                asList("properties.firstName", "properties.lastName"),
                tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max, Sort.sort("properties.lastName.sortable"))
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

  @AdminOnly
  @Override
  public void delete(UUID id) {
    editorPersonInRoleService.deletePersonInRoles(id);
    publicPersonInRoleService.deletePersonInRoles(id);

    nodes.delete(new NodeId(id, "Person"));
  }
}
