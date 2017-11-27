package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.StudyGroup;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.annotation.AdminOnly;
import fi.thl.thldtkk.api.metadata.security.annotation.UserCanCreateAdminCanUpdate;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.StudyGroupService;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException.entityNotFound;
import static java.util.stream.Collectors.toList;

public class StudyGroupServiceImpl implements StudyGroupService {

  private final Repository<NodeId, Node> nodes;

  public StudyGroupServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<StudyGroup> findAll() {
    return nodes.query(keyValue("type.id", StudyGroup.TERMED_NODE_CLASS))
      .map(StudyGroup::new)
      .collect(toList());
  }

  @Override
  public List<StudyGroup> find(String query, int max) {
    return nodes.query(
      and(
        keyValue("type.id", StudyGroup.TERMED_NODE_CLASS),
        keyWithAllValues("properties.prefLabel", tokenizeAndMap(query, t -> t + "*"))
      ), max)
      .map(StudyGroup::new)
      .collect(toList());
  }

  @Override
  public Optional<StudyGroup> get(UUID id) {
    return nodes.get(new NodeId(id, StudyGroup.TERMED_NODE_CLASS))
      .map(StudyGroup::new);
  }

  @UserCanCreateAdminCanUpdate
  @Override
  public StudyGroup save(@P("entity") StudyGroup studyGroup) {
    return new StudyGroup(nodes.save(studyGroup.toNode()));
  }

  @AdminOnly
  @Override
  public void delete(UUID id) {
    StudyGroup studyGroup = get(id).orElseThrow(entityNotFound(StudyGroup.class, id));
    nodes.delete(new NodeId(studyGroup.toNode()));
  }

}
