package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import fi.thl.thldtkk.api.metadata.domain.Study;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EditorStudyService extends Service<UUID, Study> {

  List<Study> find(UUID organizationId, String query, int max, String sortString);

  List<Study> getOrganizationUnitStudies(UUID organizationUnitId);

  Optional<Dataset> getDataset(UUID studyId, UUID datasetId);

  Dataset saveDataset(UUID studyId, Dataset dataset);

  Dataset saveDatasetAndInstanceVariables(UUID studyId, Dataset dataset);

  void deleteDataset(UUID studyId, UUID datasetId);

  Optional<InstanceVariable> getInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId);
  
  InstanceVariable saveInstanceVariable(UUID studyId, UUID datasetId, InstanceVariable instanceVariable);

  void deleteInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId);

  String getNextInstanceVariable(UUID studyId, UUID datasetId, UUID instanceVariableId);

  HttpEntity<byte[]> getExampleInstanceVariablesCsv(String encoding) throws IOException, URISyntaxException;

}
