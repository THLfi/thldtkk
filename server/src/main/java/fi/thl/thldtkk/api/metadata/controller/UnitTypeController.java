package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.UnitType;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/unitTypes")
public class UnitTypeController {

    @Autowired
    private Service<UUID, UnitType> unitTypeService;

    @Autowired
    private Service<NodeId, Node> nodeService;
    
    @GetJsonMapping
    public List<UnitType> query(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return unitTypeService.query(query).collect(toList());
    }

    @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public UnitType save(@RequestBody @Valid UnitType unitType) {
        return unitTypeService.save(unitType);
    }
    
    @DeleteMapping("/{unitTypeId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUnitType(@PathVariable("unitTypeId") UUID unitTypeId){
      unitTypeService.delete(unitTypeId);
    }

    @GetJsonMapping("/{unitTypeId}/datasets")
    public List<Dataset> getDatasets(
            @PathVariable("unitTypeId") UUID unitTypeId) {
      return nodeService.query(
        select("id", "type", "properties.*", "references.*"),
        and(keyValue("type.id", "DataSet"),
            keyValue("references.unitType.id", unitTypeId.toString())))
        .map(Dataset::new).collect(Collectors.toList());
    }
    
    @GetJsonMapping("/{unitTypeId}/instanceVariables")
    public List<InstanceVariable> getInstanceVariables(
            @PathVariable("unitTypeId") UUID unitTypeId) {
      return nodeService.query(
        select("id", "type", "properties.*", "references.*"),
        and(keyValue("type.id", "InstanceVariable"),
            keyValue("references.unitType.id", unitTypeId.toString())))
        .map(InstanceVariable::new).collect(Collectors.toList());

    }
}
