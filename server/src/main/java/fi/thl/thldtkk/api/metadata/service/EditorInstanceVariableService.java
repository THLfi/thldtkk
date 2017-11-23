package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.InstanceVariable;

import java.util.List;
import java.util.UUID;

public interface EditorInstanceVariableService extends Service<UUID, InstanceVariable> {

  List<InstanceVariable> getInstancesVariablesByVariable(UUID variableId, int max);

  List<InstanceVariable> getInstanceVariablesByUnitType(UUID unitTypeId);

  List<InstanceVariable> getInstanceVariablesByCodeList(UUID codeListId);
  
}
