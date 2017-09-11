package fi.thl.thldtkk.api.metadata.controller.v3;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import fi.thl.thldtkk.api.metadata.domain.Variable;
import fi.thl.thldtkk.api.metadata.service.v3.VariableService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "API for variables")
@RestController
@RequestMapping("/api/v3/variables")
public class VariableController3 {

  @Autowired
  private VariableService variableService;

  @ApiOperation("List all variables")
  @GetJsonMapping
  public List<Variable> query(
      @RequestParam(value = "query", defaultValue = "") String query,
      @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return variableService.find(query, max);
  }

  @ApiOperation("Get one variable by ID")
  @GetJsonMapping("/{variableId}")
  public Variable getById(@PathVariable("variableId") UUID variableId) {
    return variableService.get(variableId).orElseThrow(NotFoundException::new);
  }

  @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
  public Variable save(@RequestBody @Valid Variable variable) {
    return variableService.save(variable);
  }

  @DeleteMapping("/{variableId}")
  @ResponseStatus(NO_CONTENT)
  public void delete(@PathVariable("variableId") UUID variableId) {
    variableService.delete(variableId);
  }

}
