package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/instanceQuestions")
public class InstanceQuestionController {
  
  @Autowired
  private Service<UUID, InstanceQuestion> instanceQuestionService;

  @Autowired
  private Service<UUID, Dataset> datasetService;
  
  @GetJsonMapping("/{instanceQuestionId}")
  public InstanceQuestion getById(@PathVariable("instanceQuestionId") UUID instanceQuestionId) {
    return instanceQuestionService.get(instanceQuestionId)
      .orElseThrow(NotFoundException::new);
  }

  @GetJsonMapping("/dataset/{datasetId}/")
  public List<InstanceQuestion> getDatasetInstanceQuestions(
      @PathVariable("datasetId") UUID datasetId,
      @RequestParam(value = "query", required = false, defaultValue = "") String query) {  

    Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);
    Set<InstanceQuestion> datasetQuestions = dataset.getInstanceVariables().stream()
            .filter(instanceVariable -> !instanceVariable.getInstanceQuestions().isEmpty())
            .map(instanceVariable -> instanceVariable.getInstanceQuestions())
            .flatMap(instanceQuestions -> instanceQuestions.stream())
            .collect(Collectors.toSet());

    return instanceQuestionService.query(query)
            .filter(question -> datasetQuestions.contains(question))
            .collect(Collectors.toList());

  }
  
  @GetJsonMapping
  public List<InstanceQuestion> query(
    @RequestParam(value = "query", required = false, defaultValue = "") String query) {
    return instanceQuestionService.query(query).collect(toList());
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public InstanceQuestion save(@RequestBody @Valid InstanceQuestion instanceQuestion) {
    return instanceQuestionService.save(instanceQuestion);
  }
}
