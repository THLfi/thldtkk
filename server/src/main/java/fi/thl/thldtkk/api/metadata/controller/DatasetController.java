package fi.thl.thldtkk.api.metadata.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.service.DatasetService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.PostJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Arrays.stream;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import org.springframework.http.MediaType;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v2/datasets")
public class DatasetController {

    private static final Logger LOG = LoggerFactory.getLogger(DatasetController.class);

    @Autowired
    private DatasetService datasetService;

    private Pattern nonWordChar = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

    @GetJsonMapping
    public List<Dataset> queryDatasets(
            @RequestParam(name = "organizationId", required = false) UUID organizationId,
            @RequestParam(name = "datasetTypeId", required = false) UUID datasetTypeId,
            @RequestParam(name = "query", defaultValue = "") String query) {

        List<String> datasetQueryClauses = new ArrayList<>();

        if (organizationId != null) {
            datasetQueryClauses.add("references.owner.id:" + organizationId);
        }
        if (datasetTypeId != null) {
            datasetQueryClauses.add("references.datasetType.id:" + datasetTypeId);
        }
        if (!query.isEmpty()) {
            stream(nonWordChar.split(query)).forEach(
                    token -> datasetQueryClauses.add("properties.prefLabel:" + token + "*"));
        }

        return datasetService.query(String.join(" AND ", datasetQueryClauses)).collect(toList());
    }

    @GetJsonMapping("/{datasetId}")
    public Dataset getDataset(@PathVariable("datasetId") UUID datasetId) {
        return datasetService.get(datasetId).orElseThrow(NotFoundException::new);
    }

    @PostJsonMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public Dataset postDataset(
            @RequestParam(name = "saveInstanceVariables", defaultValue = "true") boolean saveInstanceVariables,
            @RequestBody @Valid Dataset dataset) {

        Optional<Dataset> oldDataset = Optional.empty();

        if (dataset.getId() != null) {
            oldDataset = datasetService.get(dataset.getId());
        }

        if (!saveInstanceVariables && oldDataset.isPresent()) {
            return datasetService.save(
                    new Dataset(dataset, oldDataset.get().getInstanceVariables()));
        }

        return datasetService.save(dataset);
    }

    @DeleteMapping("/{datasetId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteDataset(@PathVariable("datasetId") UUID datasetId) {
        datasetService.delete(datasetId);
    }

    @RequestMapping(
            value = "/xml-import",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Dataset generateDatasetFromXML(MultipartHttpServletRequest request) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile multipartFile = request.getFile(itr.next());
        try {
            return new XmlMapper().readValue(multipartFile.getBytes(), Dataset.class);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            return new Dataset();
        }
    }
    
    @GetJsonMapping(path="/published/recent/{maxResults}")
    public List<Dataset> getRecentAndPublishedDatasets(
            @PathVariable("maxResults") int maxResults) {
        return datasetService.getRecentAndPublished(maxResults).collect(toList());
    }
    
}
