package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Person;
import fi.thl.thldtkk.api.metadata.domain.PersonInRole;
import fi.thl.thldtkk.api.metadata.service.PersonService;
import fi.thl.thldtkk.api.metadata.service.EditorPersonInRoleService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/persons")
public class PersonController {

  @Autowired
  private PersonService personService;

  @Autowired
  private EditorPersonInRoleService personInRoleService;

  @GetJsonMapping
  public List<Person> query(@RequestParam(value = "query", defaultValue = "") String query,
                            @RequestParam(value = "max", defaultValue = "-1") Integer max) {
    return personService.find(query, max);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Person save(@RequestBody @Valid Person person) {
    return personService.save(person);
  }

  @DeleteMapping("/{personId}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("personId") UUID personId) {
    personService.delete(personId);
  }

  @GetJsonMapping("/{personId}/roles")
  public List<PersonInRole> getRoles(@PathVariable UUID personId) {
    return personInRoleService.getPersonInRoles(personId);
  }

}
