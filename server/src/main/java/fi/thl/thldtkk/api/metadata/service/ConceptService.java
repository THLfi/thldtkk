package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.Concept;

import java.util.Optional;
import java.util.UUID;

public interface ConceptService extends Service<UUID, Concept> {

    Optional<Concept> findByPrefLabel(String prefLabel);
}
