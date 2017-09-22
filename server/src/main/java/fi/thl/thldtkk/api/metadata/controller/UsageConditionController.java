package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.UsageCondition;
import fi.thl.thldtkk.api.metadata.service.UsageConditionService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/usageConditions")
public class UsageConditionController {

  @Autowired
  private UsageConditionService usageConditionService;

  @GetJsonMapping
  public List<UsageCondition> query() {
    return usageConditionService.findAll();
  }

  @GetJsonMapping("/{usageCondition}")
  public UsageCondition getById(@PathVariable("usageCondition") UUID usageConditionId) {
    return usageConditionService.get(usageConditionId).orElseThrow(NotFoundException::new);
  }

}
