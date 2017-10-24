package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.service.DatasetPublishingService;
import fi.thl.thldtkk.api.metadata.service.EditorDatasetService;
import fi.thl.thldtkk.api.metadata.service.PublicDatasetService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;

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
    Dataset savedEditorDataset = editorDatasetService.save(dataset);

    try {
      removeNonPublicPropertiesAndReferences(savedEditorDataset);
      publicDatasetService.save(dataset);
    }
    catch (Exception e) {
      log.warn("Failed to save dataset '{}' into public graph", datasetId, e);

      dataset.setPublished(false);
      savedEditorDataset = editorDatasetService.save(dataset);
    }

    // Return dataset saved in the editor graph (instead of public one)
    // because returned value is used in editor.

    return savedEditorDataset;
  }

  private void removeNonPublicPropertiesAndReferences(Dataset savedEditorDataset) {
    savedEditorDataset.setComment(null);
    savedEditorDataset.setPredecessors(Collections.emptyList());
  }

  @Override
  public Dataset withdraw(UUID datasetId) {
    Dataset dataset = editorDatasetService.get(datasetId).orElseThrow(NotFoundException::new);

    log.info("Withdrawing dataset {}", datasetId);

    dataset.setPublished(false);
    Dataset savedEditorDataset = editorDatasetService.save(dataset);

    try {
      publicDatasetService.delete(datasetId);
    }
    catch (Exception e) {
      log.warn("Failed to delete dataset '{}' from public graph", datasetId, e);

      dataset.setPublished(true);
      savedEditorDataset = editorDatasetService.save(dataset);
    }

    return savedEditorDataset;
  }

}
