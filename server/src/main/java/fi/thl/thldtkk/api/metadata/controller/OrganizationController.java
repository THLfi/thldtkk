package fi.thl.thldtkk.api.metadata.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

  @Autowired
  private RestTemplate restTemplate;

  private String organizationsPath = "/types/Organization/nodes";

  @GetJsonMapping
  public JsonArray query(
      @RequestParam(name = "query", defaultValue = "", required = false) String query,
      @RequestParam(name = "max", defaultValue = "", required = false) Integer max) {

    String url = fromPath(organizationsPath)
        .queryParam("query", query)
        .queryParam("max", max)
        .queryParam("sort", "properties.prefLabel.fi.sortable")
        .toUriString();

    return restTemplate.getForObject(url, JsonArray.class);
  }

  @GetJsonMapping("/{id}")
  public JsonObject get(@PathVariable("id") UUID id) {
    String url = fromPath(organizationsPath).path("/" + id.toString()).toUriString();

    return restTemplate.getForObject(url, JsonObject.class);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public JsonObject post(@RequestBody JsonObject dataset) {
    String url = fromPath(organizationsPath).toUriString();

    return restTemplate.postForObject(url, dataset, JsonObject.class);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("id") UUID id) {
    String url = fromPath(organizationsPath).path("/" + id.toString()).toUriString();

    restTemplate.delete(url);
  }

}
