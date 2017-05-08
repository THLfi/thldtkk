package fi.thl.thldtkk.api.metadata.controller;

import com.google.gson.JsonArray;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.util.UriComponentsBuilder.fromPath;

@RestController
@RequestMapping("/usageconditions")
public class UsageConditionController {
  @Autowired
  private RestTemplate restTemplate;

  @GetJsonMapping
  public JsonArray query(
    @RequestParam(name = "query", defaultValue = "", required = false) String query,
    @RequestParam(name = "max", defaultValue = "", required = false) Integer max) {

    String url = fromPath("/types/UsageCondition/nodes")
      .queryParam("query", query)
      .queryParam("max", max)
      .queryParam("sort", "properties.prefLabel.fi.sortable")
      .toUriString();

    return restTemplate.getForObject(url, JsonArray.class);
  }
}
