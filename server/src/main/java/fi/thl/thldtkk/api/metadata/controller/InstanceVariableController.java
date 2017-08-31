package fi.thl.thldtkk.api.metadata.controller;

import static com.google.common.base.MoreObjects.firstNonNull;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.service.InstanceVariableService;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.service.csv.InstanceVariableCsvParser;
import fi.thl.thldtkk.api.metadata.service.csv.ParsingResult;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.UUID.randomUUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "API for instance variables")
@RestController
@RequestMapping("/api/v2")
public class InstanceVariableController {

    @Autowired
    private Service<UUID, Dataset> datasetService;
    @Autowired
    private InstanceVariableService instanceVariableService;
    @Autowired
    private InstanceVariableCsvParser csvParser;

    @ApiOperation("List instance variables of given dataset")
    @GetJsonMapping("/datasets/{datasetId}/instanceVariables")
    public List<InstanceVariable> getDatasetInstanceVariables(
            @PathVariable("datasetId") UUID datasetId) {
        Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);

        return dataset.getInstanceVariables();
    }

    @ApiOperation("Get one instance variable by ID")
    @GetJsonMapping("/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
    public InstanceVariable getDatasetInstanceVariable(
            @PathVariable("datasetId") UUID datasetId,
            @PathVariable("instanceVariableId") UUID instanceVariableId) {
        return instanceVariableService.get(instanceVariableId)
                .orElseThrow(NotFoundException::new);
    }

    @PostJsonMapping(path = "/datasets/{datasetId}/instanceVariables",
            headers = "content-type=application/json",
            produces = APPLICATION_JSON_UTF8_VALUE)
    public InstanceVariable saveInstanceVariableAsJson(
            @PathVariable("datasetId") UUID datasetId,
            @RequestBody @Valid InstanceVariable instanceVariable) {

        Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);

        UUID instanceVariableId = firstNonNull(instanceVariable.getId(), randomUUID());
        instanceVariable.setId(instanceVariableId);

        Map<UUID, InstanceVariable> variablesById
                = index(dataset.getInstanceVariables(), InstanceVariable::getId);
        variablesById.put(instanceVariable.getId(), instanceVariable);

        dataset = datasetService.save(new Dataset(dataset, new ArrayList<>(variablesById.values())));

        return dataset.getInstanceVariables()
                .stream()
                .filter(iv -> instanceVariableId.equals(iv.getId()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @RequestMapping(
            path = "/datasets/{datasetId}/instanceVariables",
            method = RequestMethod.POST,
            headers = "content-type=text/csv",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<ParsingResult<List<ParsingResult<InstanceVariable>>>> importInstanceVariablesAsCsv(
            @PathVariable("datasetId") UUID datasetId,
            @RequestHeader("content-type") String contentType,
            HttpServletRequest request) throws IOException {

        Dataset dataset = datasetService.get(datasetId).orElseThrow(NotFoundException::new);

        ParsingResult<List<ParsingResult<InstanceVariable>>> parsingResult = csvParser.parse(request.getInputStream(), getCharset(contentType));
        if (parsingResult.getParsedObject().get().isEmpty()) {
            return new ResponseEntity<>(parsingResult, HttpStatus.BAD_REQUEST);
        }

        List<InstanceVariable> instanceVariables = getInstanceVariables(parsingResult);

        datasetService.save(new Dataset(dataset, instanceVariables));

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

    private List<InstanceVariable> getInstanceVariables(ParsingResult<List<ParsingResult<InstanceVariable>>> instanceVariableResults) {
        List<InstanceVariable> instanceVariables = instanceVariableResults.getParsedObject().get()
                .stream()
                .filter(result -> result.getParsedObject().isPresent())
                .map(result -> result.getParsedObject().get())
                .collect(Collectors.toList());

        instanceVariables.forEach(iv -> iv.setId(randomUUID()));

        return instanceVariables;
    }

    @DeleteMapping("/datasets/{datasetId}/instanceVariables/{instanceVariableId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDatasetInstanceVariable(
            @PathVariable("datasetId") UUID datasetId,
            @PathVariable("instanceVariableId") UUID instanceVariableId) {

        Dataset dataset = datasetService.get(datasetId).orElseThrow(
                NotFoundException::new);

        List<InstanceVariable> variables = new ArrayList<>();
        variables.addAll(dataset.getInstanceVariables());
        variables.removeIf(v -> v.getId().equals(instanceVariableId));

        datasetService.save(new Dataset(dataset, variables));
    }

    @ApiOperation("List all instance variables")
    @GetJsonMapping("/instanceVariables")
    public List<InstanceVariable> getInstanceVariables(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "max", required = false, defaultValue = "10") Integer maxResults,
            @RequestParam(value = "requirePublished", required = false, defaultValue = "false") boolean requirePublished,
            @RequestParam(value = "broadSearch", required = false, defaultValue = "false") boolean broadSearch) {

        if (broadSearch) {
            try {
                return instanceVariableService.extendedQuery(query, maxResults)
                        .filter(instanceVariable -> !requirePublished || (instanceVariable.getDataset().isPresent()
                                && instanceVariable.getDataset().get().isPublished().isPresent()
                                && instanceVariable.getDataset().get().isPublished().get()))
                        .collect(Collectors.toList());
            } // TODO: what to do with e.g. instance variables with invalid non-ISO reference dates?
            catch (DateTimeParseException dateTimeEx) {
                return new ArrayList<>();
            }
        } else {
            return instanceVariableService.query(query, maxResults)
                    .filter(instanceVariable -> !requirePublished || (instanceVariable.getDataset().isPresent()
                            && instanceVariable.getDataset().get().isPublished().isPresent()
                            && instanceVariable.getDataset().get().isPublished().get()))
                    .collect(Collectors.toList());
        }
    }

}
