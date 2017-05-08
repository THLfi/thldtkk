package fi.thl.thldtkk.api.metadata.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/v2/datasets")
public class DatasetController2 {

  @Autowired
  private Service<Dataset> datasetService;

  @GetJsonMapping
  public List<Dataset> query() {
    return datasetService.query().collect(toList());
  }

  @GetJsonMapping("/{id}")
  public Dataset get(@PathVariable("id") UUID id) {
    return datasetService.get(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Dataset post(@RequestBody Dataset dataset) {
    return datasetService.save(dataset);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    datasetService.delete(id);
  }

}