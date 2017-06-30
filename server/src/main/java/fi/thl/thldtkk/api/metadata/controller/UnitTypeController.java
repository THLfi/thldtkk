package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v2/unitTypes")
public class UnitTypeController {

    @Autowired
    private Service<UUID, UnitType> unitTypeService;

    @GetJsonMapping
    public List<UnitType> query(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return unitTypeService.query(query).collect(toList());
    }

    @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public UnitType save(@RequestBody @Valid UnitType unitType) {
        return unitTypeService.save(unitType);
    }

}
