package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.DatasetPublishingService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;

public class DatasetPublishingServiceImpl implements DatasetPublishingService {

  private Logger log = LoggerFactory.getLogger(getClass());

  private EditorDatasetService editorDatasetService;
  private PublicDatasetService publicDatasetService;

  public DatasetPublishingServiceImpl(
      EditorDatasetService editorDatasetService,
      PublicDatasetService publicDatasetService) {
    this.editorDatasetService = editorDatasetService;
    this.publicDatasetService = publicDatasetService;
  }

  @Override
  public Dataset publish(UUID datasetId) {
    Dataset dataset = editorDatasetService.get(datasetId).orElseThrow(NotFoundException::new);

    log.info("Publishing dataset {}", dataset.getId());

    dataset.setPublished(true);
    Dataset savedDataset = editorDatasetService.save(dataset);

    publicDatasetService.save(dataset);

    return savedDataset;
  }

  @Override
  public Dataset withdraw(UUID datasetId) {
    Dataset dataset = editorDatasetService.get(datasetId).orElseThrow(NotFoundException::new);

    log.info("Withdrawing dataset {}", datasetId);

    dataset.setPublished(false);
    Dataset savedDataset = editorDatasetService.save(dataset);

    publicDatasetService.delete(datasetId);
    return savedDataset;
  }

}
