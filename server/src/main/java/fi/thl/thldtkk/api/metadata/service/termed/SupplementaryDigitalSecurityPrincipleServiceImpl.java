package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.SupplementaryDigitalSecurityPrinciple;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.SupplementaryDigitalSecurityPrincipleService;
import fi.thl.thldtkk.api.metadata.util.Tokenizer;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.stream.Collectors.toList;

public class SupplementaryDigitalSecurityPrincipleServiceImpl implements SupplementaryDigitalSecurityPrincipleService {

  private Repository<NodeId, Node> nodes;

  public SupplementaryDigitalSecurityPrincipleServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<SupplementaryDigitalSecurityPrinciple> findAll() {
    return find("", -1);
  }

  @Override
  public List<SupplementaryDigitalSecurityPrinciple> find(String query, int max) {
    Criteria criteria = query.isEmpty() ?
      keyValue("type.id", SupplementaryDigitalSecurityPrinciple.TERMED_NODE_CLASS)
      : and(
      keyValue("type.id", SupplementaryDigitalSecurityPrinciple.TERMED_NODE_CLASS),
      keyWithAllValues("properties.prefLabel", Tokenizer.tokenizeAndMap(query, t -> t + "*")));

    return nodes.query(criteria, max)
      .map(SupplementaryDigitalSecurityPrinciple::new)
      .collect(toList());
  }

  @Override
  public Optional<SupplementaryDigitalSecurityPrinciple> get(UUID id) {
    return nodes.get(new NodeId(id, SupplementaryDigitalSecurityPrinciple.TERMED_NODE_CLASS))
      .map(SupplementaryDigitalSecurityPrinciple::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public SupplementaryDigitalSecurityPrinciple save(@P("entity") SupplementaryDigitalSecurityPrinciple principle) {
    return new SupplementaryDigitalSecurityPrinciple(nodes.save(principle.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    SupplementaryDigitalSecurityPrinciple principle = get(id).orElseThrow(NotFoundException.entityNotFound(SupplementaryDigitalSecurityPrinciple.class, id));
    nodes.delete(new NodeId(principle.toNode()));
  }
}
