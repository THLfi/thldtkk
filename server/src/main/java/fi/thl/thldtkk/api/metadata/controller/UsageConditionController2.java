package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.UsageCondition;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/usageConditions")
public class UsageConditionController2 {

    @Autowired
    private Service<UUID, UsageCondition> usageConditionService;

    @GetJsonMapping
    public List<UsageCondition> queryUsageConditions() {
        return usageConditionService.query().collect(toList());
    }

    @GetJsonMapping("/{usageCondition}")
    public UsageCondition getDataset(@PathVariable("usageCondition") UUID usageConditionId) {
        return usageConditionService.get(usageConditionId).orElseThrow(NotFoundException::new);
    }

}
