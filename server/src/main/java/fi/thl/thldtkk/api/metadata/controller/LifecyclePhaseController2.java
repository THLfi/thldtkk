package fi.thl.thldtkk.api.metadata.controller;

import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.LifecyclePhase;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/lifecyclePhases")
public class LifecyclePhaseController2 {

  @Autowired
  private Service<UUID, LifecyclePhase> lifecyclePhaseService;

  @GetJsonMapping
  public List<LifecyclePhase> queryLifecyclePhases() {
    return lifecyclePhaseService.query().collect(toList());
  }

  @GetJsonMapping("/{lifecyclePhase}")
  public LifecyclePhase getDataset(@PathVariable("lifecyclePhase") UUID lifecyclePhaseId) {
    return lifecyclePhaseService.get(lifecyclePhaseId).orElseThrow(NotFoundException::new);
  }

}
