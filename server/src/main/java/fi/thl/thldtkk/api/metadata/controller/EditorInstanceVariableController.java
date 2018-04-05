package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.EditorInstanceVariableService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@Api(description = "Editor API for instance variables")
@RestController
@RequestMapping(API.PATH_WITH_VERSION + "/editor")
public class EditorInstanceVariableController {

  @Autowired
  private EditorInstanceVariableService instanceVariableService;

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getVariableInstanceVariables(
      @PathVariable("variableId") UUID variableId) {
    return instanceVariableService.getInstancesVariablesByVariable(variableId, -1);
  }

  @ApiOperation("List all instance variables of given code list")
  @GetJsonMapping("/codeLists/{codeListId}/instanceVariables")
  public List<InstanceVariable> getCodeListInstanceVariables(
          @PathVariable("codeListId") UUID codeListId) {
    return instanceVariableService.getInstanceVariablesByCodeList(codeListId);
  }

  @ApiOperation("Search instance variables")
  @GetJsonMapping("/instanceVariables")
  public List<InstanceVariable> getInstanceVariables(
          @RequestParam(value = "query", defaultValue = "") String query,
          @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return instanceVariableService.find(query, max);
  }

}
