package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;
import fi.thl.thldtkk.api.metadata.service.PublicInstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.csv.CsvFileNameBuilder;
import fi.thl.thldtkk.api.metadata.service.csv.GeneratorResult;
import fi.thl.thldtkk.api.metadata.service.csv.InstanceVariableCsvGenerator;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "Public API for instance variables")
@RestController
@RequestMapping("/api/v3/public")
public class PublicInstanceVariableController {

  @Autowired
  private PublicInstanceVariableService instanceVariableService;
  
  @Autowired
  private PublicDatasetService datasetService;
          
  @ApiOperation("List all instance variables of given dataset")
  @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
  public List<InstanceVariable> getInstanceVariablesOfDataset(@PathVariable("datasetId") UUID id) {
    return instanceVariableService.getDatasetInstanceVariables(id);
  }

  @ApiOperation("Get instance variable by ID")
  @GetJsonMapping({
      "/datasets/{datasetId}/instanceVariables/{id}",
      "/instanceVariables/{id}"})
  public InstanceVariable getInstanceVariable(@PathVariable("id") UUID id) {
    return instanceVariableService.get(id).orElseThrow(NotFoundException::new);
  }

  @ApiOperation("List all instances of given variable")
  @GetJsonMapping("/variables/{variableId}/instanceVariables")
  public List<InstanceVariable> getInstancesOfVariable(
      @PathVariable("variableId") UUID variableId) {
    return instanceVariableService.getVariableInstancesVariables(variableId, -1);
  }

  @ApiOperation("Search instance variables")
  @GetJsonMapping("/instanceVariables")
  public List<InstanceVariable> getInstanceVariables(
      @RequestParam(value = "query", defaultValue = "") String query,
      @RequestParam(value = "max", defaultValue = "10") Integer max) {
    return instanceVariableService.find(query, max);
  }
  
  @RequestMapping(
          value = "/datasets/{datasetId}/instanceVariables.csv", 
          method = RequestMethod.GET)
  @ResponseBody
  public byte[] getInstanceVariablesOfDatasetAsCsv(
          @PathVariable("datasetId") UUID datasetId, 
          @RequestParam(value = "lang", defaultValue = "fi") String language,
          @RequestParam(value = "encoding", defaultValue = "ISO-8859-15") String encoding,
          HttpServletResponse response) {

    List<InstanceVariable> instanceVariables = instanceVariableService.getDatasetInstanceVariables(datasetId);
    Optional<Dataset> dataset = datasetService.get(datasetId);
    
    if(dataset.isPresent()) {
     instanceVariables = instanceVariables.stream()
             .map(iv -> {
               iv.setDataset(dataset.get());
               return iv;
        }).collect(Collectors.toList());
    }

    InstanceVariableCsvGenerator generator = new InstanceVariableCsvGenerator(instanceVariables, language, encoding);
    GeneratorResult result = generator.generate();

    if(result.getData().isPresent()) {
      String fileName = CsvFileNameBuilder.getInstanceVariableExportFileName(dataset, language);
      
      response.setHeader("Content-Disposition", "attachment; filename="+fileName+".csv");
      response.setHeader("Content-Type", "text/csv; charset="+encoding);
      return result.getData().get();
    }
    else {
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return new byte[0];
    }
    
  }

}
