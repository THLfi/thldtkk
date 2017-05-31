package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Variable;
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
@RequestMapping("/api/v2/variables")
public class VariableController {

    @Autowired
    private Service<UUID, Variable> variableService;

    @GetJsonMapping
    public List<Variable> queryVariables() {
        return variableService.query().collect(toList());
    }

    @GetJsonMapping("/{variable}")
    public Variable getVariable(@PathVariable("variable") UUID variableId) {
        return variableService.get(variableId).orElseThrow(NotFoundException::new);
    }
}
