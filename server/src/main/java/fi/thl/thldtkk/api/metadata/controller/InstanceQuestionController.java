package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.service.InstanceQuestionService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/instanceQuestions")
public class InstanceQuestionController {

  @Autowired
  private InstanceQuestionService instanceQuestionService;

  @GetJsonMapping("/{instanceQuestionId}")
  public InstanceQuestion getById(@PathVariable("instanceQuestionId") UUID instanceQuestionId) {
    return instanceQuestionService.get(instanceQuestionId).orElseThrow(NotFoundException::new);
  }

  @GetJsonMapping("/studies/{studyId}/datasets/{datasetId}")
  public List<InstanceQuestion> getDatasetInstanceQuestions(
      @PathVariable("studyId") UUID studyId,
      @PathVariable("datasetId") UUID datasetId,
      @RequestParam(value = "query", defaultValue = "") String query) {
    return instanceQuestionService.findDatasetInstanceQuestions(studyId, datasetId, query);
  }

  @GetJsonMapping
  public List<InstanceQuestion> query(
      @RequestParam(value = "query", defaultValue = "") String query) {
    return instanceQuestionService.find(query, -1);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public InstanceQuestion save(@RequestBody @Valid InstanceQuestion instanceQuestion) {
    return instanceQuestionService.save(instanceQuestion);
  }

}
