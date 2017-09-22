package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Unit;
import java.util.List;
import java.util.UUID;

public interface UnitService extends Service<UUID, Unit> {

  List<Unit> findBySymbol(String symbol);

}
