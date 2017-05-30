package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.service.ConceptService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v2/concepts")
public class ConceptController {
  private ConceptService conceptService;

  @Autowired
  public ConceptController(ConceptService conceptService) {
    this.conceptService = conceptService;
  }

  @GetJsonMapping
  public List<Concept> queryConcepts(
    @RequestParam(value = "query", required = false, defaultValue = "") String query,
    @RequestParam(value = "max", required = false, defaultValue = "10") Integer maxResults) {
    return conceptService.query(query, maxResults).collect(toList());
  }
}
