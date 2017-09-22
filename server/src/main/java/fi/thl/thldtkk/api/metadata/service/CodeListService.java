package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.CodeList;

import java.util.List;
import java.util.UUID;

public interface CodeListService extends Service<UUID, CodeList> {
  List<CodeList> findByExactPrefLabel(String prefLabel, int max);
  List<CodeList> findByExactReferenceId(String referenceId, int max);
}
