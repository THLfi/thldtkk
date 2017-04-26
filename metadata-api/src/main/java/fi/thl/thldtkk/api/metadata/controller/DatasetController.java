package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/datasets")
public class DatasetController {

  @Autowired
  private RestTemplate restTemplate;

  private String datasetsPath = "/types/DataSet/nodes";

  @GetJsonMapping
  public String query(
      @RequestParam(name = "query", defaultValue = "", required = false) String query,
      @RequestParam(name = "max", defaultValue = "", required = false) Integer max) {

    String url = fromPath(datasetsPath)
        .queryParam("query", query)
        .queryParam("max", max)
        .queryParam("sort", "properties.prefLabel.fi.sortable")
        .toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @GetJsonMapping("/{id}")
  public String get(@PathVariable("id") UUID id) {
    String url = fromPath(datasetsPath).path("/" + id.toString()).toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @GetJsonMapping("/{id}/owners")
  public String getDatasetOwners(@PathVariable("id") UUID id) {
    String url = fromPath(datasetsPath)
        .path("/" + id.toString())
        .path("/references/owner")
        .toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @GetJsonMapping("/{id}/instanceVariables")
  public String getDatasetInstanceVariables(@PathVariable("id") UUID id) {
    String url = fromPath(datasetsPath)
        .path("/" + id.toString())
        .path("/references/instanceVariable")
        .toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public String post(String dataset) {
    String url = fromPath(datasetsPath).toUriString();

    return restTemplate.postForObject(url, dataset, String.class);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    String url = fromPath(datasetsPath).path("/" + id.toString()).toUriString();

    restTemplate.delete(url);
  }

}
