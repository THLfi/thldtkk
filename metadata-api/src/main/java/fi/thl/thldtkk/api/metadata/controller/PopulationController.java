package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
@RequestMapping("/populations")
public class PopulationController {

  @Autowired
  private RestTemplate restTemplate;

  private String populationsPath = "/types/Population/nodes";

  @GetJsonMapping
  public String query(
    @RequestParam(name = "query", defaultValue = "", required = false) String query,
    @RequestParam(name = "max", defaultValue = "", required = false) Integer max) {

    String url = fromPath(populationsPath)
      .queryParam("query", query)
      .queryParam("max", max)
      .queryParam("sort", "properties.prefLabel.fi.sortable")
      .toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @GetJsonMapping("/{id}")
  public String get(@PathVariable("id") UUID id) {
    String url = fromPath(populationsPath).path("/" + id.toString()).toUriString();

    return restTemplate.getForObject(url, String.class);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<String> post(@RequestBody String population) {
    String url = fromPath(populationsPath).toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    HttpEntity<String> request = new HttpEntity<>(population, headers);

    return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    String url = fromPath(populationsPath)
      .path("/")
      .path(id.toString())
      .toUriString();

    restTemplate.delete(url);
  }

}
