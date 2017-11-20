package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.csv.InstanceVariableCsvParser;
import fi.thl.thldtkk.api.metadata.service.csv.ParsingResult;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@Api(description = "Editor CSV API for instance variables")
@RestController
@RequestMapping("/api/v3/editor")
public class EditorInstanceVariableCsvImportController {

  @Autowired
  private InstanceVariableCsvParser csvParser;
  @Autowired
  private EditorStudyService editorStudyService;

  @PostMapping(path = "/studies/{studyId}/datasets/{datasetId}/instanceVariables",
      consumes = "text/csv",
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ParsingResult<List<ParsingResult<InstanceVariable>>>> importInstanceVariablesAsCsv(
      @PathVariable("studyId") UUID studyId,
      @PathVariable("datasetId") UUID datasetId,
      @RequestHeader("content-type") String contentType,
      HttpServletRequest request) throws IOException {

    Dataset dataset = editorStudyService.getDataset(studyId, datasetId)
      .orElseThrow(NotFoundException::new);

    ParsingResult<List<ParsingResult<InstanceVariable>>> parsingResult
      = csvParser.parse(request.getInputStream(), getCharset(contentType));

    if (parsingResult.getParsedObject().get().isEmpty()) {
      return new ResponseEntity<>(parsingResult, HttpStatus.BAD_REQUEST);
    }

    dataset.setInstanceVariables(getInstanceVariables(parsingResult));

    editorStudyService.saveDatasetAndInstanceVariables(studyId, dataset);

    return new ResponseEntity<>(parsingResult, HttpStatus.OK);
  }

  private String getCharset(String contentType) {
    if (StringUtils.hasText(contentType)) {
      String charsetString = ";charset=";
      int index = contentType.indexOf(charsetString);
      if (index > -1) {
        return contentType.substring(index + charsetString.length());
      }
    }
    return null;
  }

  private List<InstanceVariable> getInstanceVariables(
      ParsingResult<List<ParsingResult<InstanceVariable>>> instanceVariableResults) {
    List<InstanceVariable> instanceVariables = instanceVariableResults.getParsedObject().get()
        .stream()
        .filter(result -> result.getParsedObject().isPresent())
        .map(result -> result.getParsedObject().get())
        .collect(Collectors.toList());

    instanceVariables.forEach(iv -> iv.setId(randomUUID()));

    return instanceVariables;
  }

}
