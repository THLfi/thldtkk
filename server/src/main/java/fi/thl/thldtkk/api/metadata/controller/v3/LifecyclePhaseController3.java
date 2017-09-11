package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.LifecyclePhase;
import fi.thl.thldtkk.api.metadata.service.v3.LifecyclePhaseService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/lifecyclePhases")
public class LifecyclePhaseController3 {

  @Autowired
  private LifecyclePhaseService lifecyclePhaseService;

  @GetJsonMapping
  public List<LifecyclePhase> queryLifecyclePhases() {
    return lifecyclePhaseService.findAll();
  }

  @GetJsonMapping("/{lifecyclePhase}")
  public LifecyclePhase getLifecyclePhase(@PathVariable("lifecyclePhase") UUID lifecyclePhaseId) {
    return lifecyclePhaseService.get(lifecyclePhaseId).orElseThrow(NotFoundException::new);
  }

}
