package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.service.EditorStudyService;
import fi.thl.thldtkk.api.metadata.service.PublicStudyService;
import fi.thl.thldtkk.api.metadata.service.StudyPublishingService;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;

public class StudyPublishingServiceImpl implements StudyPublishingService {

  private static final Logger log = LoggerFactory.getLogger(StudyPublishingServiceImpl.class);

  private final EditorStudyService editorStudyService;
  private final PublicStudyService publicStudyService;

  public StudyPublishingServiceImpl(EditorStudyService editorStudyService,
                                    PublicStudyService publicStudyService) {
    this.editorStudyService = editorStudyService;
    this.publicStudyService = publicStudyService;
  }

  @Override
  public Study publish(UUID studyId) {
    Study study = editorStudyService.get(studyId).orElseThrow(NotFoundException::new);

    log.info("Publishing study '{}'", studyId);

    study.setPublished(true);
    Study savedEditorStudy = editorStudyService.save(study);

    try {
      removeNonPublicPropertiesAndReferences(savedEditorStudy);
      publicStudyService.save(savedEditorStudy);
    }
    catch (Exception e) {
      log.warn("Failed to save study '{}' into public graph", studyId, e);

      study.setPublished(false);
      savedEditorStudy = editorStudyService.save(study);
    }

    // Return dataset saved in the editor graph (instead of public one)
    // because returned value is used in editor.

    return savedEditorStudy;
  }

  private void removeNonPublicPropertiesAndReferences(Study study) {
    study.setLastModifiedByUser(null);
    study.setComment(null);
    study.setPredecessors(Collections.emptyList());
    study.getDatasets().forEach(d -> removeNonPublicPropertiesAndReferences(d));
    study.setPhysicalLocation(Collections.emptyMap());
    study.setSystemInRoles(Collections.emptyList());
  }

  private void removeNonPublicPropertiesAndReferences(Dataset dataset) {
    dataset.setLastModifiedByUser(null);
    dataset.setComment(null);
    dataset.setPredecessors(Collections.emptyList());
    dataset.getInstanceVariables().forEach(iv -> removeNonPublicPropertiesAndReferences(iv));
  }

  private void removeNonPublicPropertiesAndReferences(InstanceVariable instanceVariable) {
    instanceVariable.setLastModifiedByUser(null);
    instanceVariable.setSource(null);
  }

  @Override
  public Study withdraw(UUID studyId) {
    Study study = editorStudyService.get(studyId).orElseThrow(NotFoundException::new);

    log.info("Withdrawing study '{}'", studyId);

    study.setPublished(false);
    Study savedStudy = editorStudyService.save(study);

    publicStudyService.delete(studyId);

    return savedStudy;
  }

}
