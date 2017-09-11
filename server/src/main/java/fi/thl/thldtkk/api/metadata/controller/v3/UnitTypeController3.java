package fi.thl.thldtkk.api.metadata.controller.v3;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.UnitType;
import fi.thl.thldtkk.api.metadata.service.v3.UnitTypeService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/unitTypes")
public class UnitTypeController3 {

  @Autowired
  private UnitTypeService unitTypeService;

  @GetJsonMapping
  public List<UnitType> query(@RequestParam(value = "query", defaultValue = "") String query) {
    return unitTypeService.find(query, -1);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public UnitType save(@RequestBody @Valid UnitType unitType) {
    return unitTypeService.save(unitType);
  }

}
