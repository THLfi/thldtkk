package fi.thl.thldtkk.api.metadata.service.v3;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;
import java.util.List;
import java.util.UUID;

public interface InstanceVariableService extends Service<UUID, InstanceVariable> {

  List<InstanceVariable> getDatasetInstanceVariables(UUID datasetId);

  List<InstanceVariable> getVariableInstancesVariables(UUID variableId);

}
