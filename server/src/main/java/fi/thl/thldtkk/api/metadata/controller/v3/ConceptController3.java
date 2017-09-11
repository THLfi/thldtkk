package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.service.v3.ConceptService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/concepts")
public class ConceptController3 {

  @Autowired
  private ConceptService conceptService;

  @GetJsonMapping
  public List<Concept> queryConcepts(
      @RequestParam(value = "query", defaultValue = "") String query,
      @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return conceptService.find(query, max);
  }
}
