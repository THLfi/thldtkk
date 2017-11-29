package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.StudyGroup;
import fi.thl.thldtkk.api.metadata.service.StudyGroupService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/api/v3/studyGroups")
public class StudyGroupController {

  @Autowired
  private StudyGroupService studyGroupService;

  @GetJsonMapping
  public List<StudyGroup> findByOwnerOrganizationId(@RequestParam UUID ownerOrganizationId) {
    return studyGroupService.findByOwnerOrganizationId(ownerOrganizationId, -1);
  }

  @GetJsonMapping("/{id}")
  public StudyGroup getById(@RequestParam("id") UUID id) {
    return studyGroupService.get(id)
      .orElseThrow(entityNotFound(StudyGroup.class, id));
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public StudyGroup save(@RequestBody @Valid StudyGroup studyGroup) {
    return studyGroupService.save(studyGroup);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("id") UUID id){
    studyGroupService.delete(id);
  }

}
