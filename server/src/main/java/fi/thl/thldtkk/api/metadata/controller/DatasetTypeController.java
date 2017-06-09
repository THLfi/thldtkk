package fi.thl.thldtkk.api.metadata.controller;

import fi.thl.thldtkk.api.metadata.domain.DatasetType;
import fi.thl.thldtkk.api.metadata.service.Service;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/datasetTypes")
public class DatasetTypeController {

    @Autowired
    private Service<UUID, DatasetType> datasetTypeService;

    @GetJsonMapping
    public List<DatasetType> queryDatasetTypes() {
        return datasetTypeService.query().collect(toList());
    }

    @GetJsonMapping("/{datasetTypeId}")
    public DatasetType getDatasetType(@PathVariable("datasetTypeId") UUID datasetTypeId) {
        return datasetTypeService.get(datasetTypeId).orElseThrow(NotFoundException::new);
    }

}
