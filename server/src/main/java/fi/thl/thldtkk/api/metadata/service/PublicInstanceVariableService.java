package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PublicInstanceVariableService extends Service<UUID, InstanceVariable> {

  List<InstanceVariable> getDatasetInstanceVariables(UUID datasetId);

  List<InstanceVariable> getVariableInstancesVariables(UUID variableId, int max);

  Optional<InstanceVariable> get(UUID id, List<String> select);
}
