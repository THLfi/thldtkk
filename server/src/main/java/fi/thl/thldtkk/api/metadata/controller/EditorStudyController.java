package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api(description = "Editor API for studies")
@RestController
@RequestMapping("/api/v3/editor/studies")
public class EditorStudyController {

  @Autowired
  private EditorStudyService editorStudyService;

  @ApiOperation("Search studies")
  @GetJsonMapping
  public List<Study> query(
      @RequestParam(name = "organizationId", required = false) UUID organizationId,
      @RequestParam(name = "query", defaultValue = "") String query,
      @RequestParam(name = "max", defaultValue = "-1") int max) {
    return editorStudyService.find(organizationId, query, max, null);
  }

  @ApiOperation("Get one study by ID")
  @GetJsonMapping("/{studyId}")
  public Study getStudy(@PathVariable("studyId") UUID studyId) {
    return editorStudyService.get(studyId).orElseThrow(NotFoundException::new);
  }

  @ApiOperation("Create a new study or update an existing study")
  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Study postStudy(@RequestBody @Valid Study study) {
    return editorStudyService.save(study);
  }

  @ApiOperation("Delete an existing study")
  @DeleteMapping("/{studyId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteStudy(@PathVariable("studyId") UUID studyId) {
    editorStudyService.delete(studyId);
  }

}
