package fi.thl.thldtkk.api.metadata.controller;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.InstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class InstanceVariableController {

  @Autowired
  private Service<UUID, Dataset> datasetService;
  @Autowired
  private InstanceVariableService instanceVariableService;

  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getDatasetInstanceVariables(
      @PathVariable("datasetId") UUID datasetId) {
    Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);

    return dataset.getInstanceVariables();
  }

  @GetJsonMapping("/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  public InstanceVariable getDatasetInstanceVariable(
      @PathVariable("datasetId") UUID datasetId,
      @PathVariable("instanceVariableId") UUID instanceVariableId) {
    return instanceVariableService.get(instanceVariableId)
      .orElseThrow(NotFoundException::new);
  }

  @PostJsonMapping(path = "/datasets/{datasetId}/instanceVariables",
      produces = APPLICATION_JSON_UTF8_VALUE)
  public InstanceVariable postDatasetInstanceVariable(
      @PathVariable("datasetId") UUID datasetId,
      @RequestBody InstanceVariable instanceVariable) {

    Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);

    UUID instanceVariableId = firstNonNull(instanceVariable.getId(), randomUUID());
    instanceVariable.setId(instanceVariableId);

    Map<UUID, InstanceVariable> variablesById =
        index(dataset.getInstanceVariables(), InstanceVariable::getId);
    variablesById.put(instanceVariable.getId(), instanceVariable);

    dataset = datasetService.save(new Dataset(dataset, new ArrayList<>(variablesById.values())));

    return dataset.getInstanceVariables()
      .stream()
      .filter(iv -> instanceVariableId.equals(iv.getId()))
      .findFirst()
      .orElseThrow(IllegalStateException::new);
  }

  @DeleteMapping("/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
  @ResponseStatus(NO_CONTENT)
  public void deleteDatasetInstanceVariable(
      @PathVariable("datasetId") UUID datasetId,
      @PathVariable("instanceVariableId") UUID instanceVariableId) {

    Dataset dataset = datasetService.get(datasetId).orElseThrow(
        NotFoundException::new);

    List<InstanceVariable> variables = new ArrayList<>();
    variables.addAll(dataset.getInstanceVariables());
    variables.removeIf(v -> v.getId().equals(instanceVariableId));

    datasetService.save(new Dataset(dataset, variables));
  }

}
