package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Unit;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/api/v2/units")
public class UnitController {

  @Autowired
  private Service<UUID, Unit> unitService;

  @GetJsonMapping("/{unitId}")
  public Unit getById(@PathVariable("unitId") UUID unitId) {
    return unitService.get(unitId)
      .orElseThrow(NotFoundException::new);
  }

  @GetJsonMapping
  public List<Unit> query(
    @RequestParam(value = "query", required = false, defaultValue = "") String query) {
    return unitService.query(query).collect(toList());
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Unit save(@RequestBody @Valid Unit unit) {
    return unitService.save(unit);
  }

}
