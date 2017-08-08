package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Universe;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v2/universes")
public class UniverseController {

    @Autowired
    private Service<UUID, Universe> universeService;

    @GetJsonMapping
    public List<Universe> query(
            @RequestParam(value = "query", required = false, defaultValue = "") String query) {
        return universeService.query(query).collect(toList());
    }

    @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public Universe save(@RequestBody @Valid Universe universe) {
        return universeService.save(universe);
    }

}
