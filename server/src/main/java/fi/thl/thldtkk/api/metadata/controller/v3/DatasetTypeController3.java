package fi.thl.thldtkk.api.metadata.controller.v3;

import fi.thl.thldtkk.api.metadata.domain.DatasetType;
import fi.thl.thldtkk.api.metadata.service.v3.DatasetTypeService;
import fi.thl.thldtkk.api.metadata.util.spring.annotation.GetJsonMapping;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v3/datasetTypes")
public class DatasetTypeController3 {

  @Autowired
  private DatasetTypeService datasetTypeService;

  @GetJsonMapping
  public List<DatasetType> query() {
    return datasetTypeService.findAll();
  }

  @GetJsonMapping("/{datasetTypeId}")
  public DatasetType findById(@PathVariable("datasetTypeId") UUID datasetTypeId) {
    return datasetTypeService.get(datasetTypeId).orElseThrow(NotFoundException::new);
  }

}
