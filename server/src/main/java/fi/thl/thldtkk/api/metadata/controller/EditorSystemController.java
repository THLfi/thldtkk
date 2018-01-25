package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.System;
import fi.thl.thldtkk.api.metadata.service.EditorSystemService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/editor/systems")
public class EditorSystemController {

  @Autowired
  private EditorSystemService systemService;

  @GetJsonMapping("/{systemId}")
  public System getSystem(@PathVariable UUID systemId) {
    return systemService.get(systemId)
      .orElseThrow(entityNotFound(System.class, systemId));
  }

  @GetJsonMapping
  public List<System> findAll() {
    return systemService.findAll();
  }

  @PostJsonMapping(
    produces = APPLICATION_JSON_UTF8_VALUE)
  public System postSystem(@RequestBody @Valid System system) {
    return systemService.save(system);
  }

}
