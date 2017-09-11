package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.Concept;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.ConceptService;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConceptServiceImpl implements ConceptService {

  private Repository<NodeId, Node> nodes;

  public ConceptServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<Concept> findAll() {
    return nodes.query(keyValue("type.id", "Concept"))
        .map(Concept::new)
        .collect(toList());
  }

  @Override
  public List<Concept> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "Concept"),
            keyWithAnyValue("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Concept::new)
        .collect(toList());
  }

  @Override
  public Optional<Concept> get(UUID id) {
    return nodes.get(new NodeId(id, "Concept")).map(Concept::new);
  }

}
