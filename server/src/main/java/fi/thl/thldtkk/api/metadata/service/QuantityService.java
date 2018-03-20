package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Quantity;

import java.util.Optional;
import java.util.UUID;

public interface QuantityService extends Service<UUID, Quantity> {

    Optional<Quantity> findByPrefLabel(String prefLabel);
}
