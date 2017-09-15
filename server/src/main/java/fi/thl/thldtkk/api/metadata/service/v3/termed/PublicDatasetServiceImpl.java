package fi.thl.thldtkk.api.metadata.service.v3.termed;

import fi.thl.thldtkk.api.metadata.domain.Dataset;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import fi.thl.thldtkk.api.metadata.domain.query.Sort;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.PublicDatasetService;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasText;


public class PublicDatasetServiceImpl implements PublicDatasetService {

  private Repository<NodeId, Node> nodes;

  public PublicDatasetServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public Optional<Dataset> get(UUID id) {
    return nodes.get(select("id", "type", "properties.*", "references.*",
        "references.inScheme:2",
        "references.conceptsFromScheme:2",
        "references.variable:2",
        "references.quantity:2",
        "references.unit:2",
        "references.codeList:2",
        "references.source:2",
        "references.instanceQuestions:2",
        "references.personInRoles:2",
        "references.person:2",
        "references.role:2"),
        new NodeId(id, "DataSet")).map(Dataset::new);
  }

  @Override
  public List<Dataset> findAll() {
    return nodes.query(keyValue("type.id", "DataSet"))
        .map(Dataset::new)
        .collect(toList());
  }

  @Override
  public List<Dataset> find(String query, int max) {
    return nodes.query(
        and(keyValue("type.id", "DataSet"),
            keyWithAnyValue("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))),
        max)
        .map(Dataset::new)
        .collect(toList());
  }

  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", "DataSet"));

    if (organizationId != null) {
      criteria.add(keyValue("references.organization.id", organizationId.toString()));
    }
    if (datasetTypeId != null) {
      criteria.add(keyValue("references.datasetType.id", datasetTypeId.toString()));
    }
    if (!query.isEmpty()) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
    }

    return nodes.query(and(criteria), max).map(Dataset::new).collect(toList());
  }
  
  @Override
  public List<Dataset> find(UUID organizationId, UUID datasetTypeId, String query, int max, String sortString) {
    List<Criteria> criteria = new ArrayList<>();

    criteria.add(keyValue("type.id", "DataSet"));

    if (organizationId != null) {
      criteria.add(keyValue("references.owner.id", organizationId.toString()));
    }
    if (datasetTypeId != null) {
      criteria.add(keyValue("references.datasetType.id", datasetTypeId.toString()));
    }
    if (!query.isEmpty()) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
    }

    if (hasText(sortString)) {
      return nodes.query(and(criteria), max, Sort.sort(sortString)).map(Dataset::new).collect(toList());
    }
    else {
      return nodes.query(and(criteria), max).map(Dataset::new).collect(toList());
    }
  }

}