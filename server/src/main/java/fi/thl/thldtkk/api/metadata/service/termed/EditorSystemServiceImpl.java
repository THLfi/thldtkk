package fi.thl.thldtkk.api.metadata.service.termed;

import fi.thl.thldtkk.api.metadata.domain.Link;
import fi.thl.thldtkk.api.metadata.domain.System;
import fi.thl.thldtkk.api.metadata.domain.query.Criteria;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.security.UserHelper;
import fi.thl.thldtkk.api.metadata.service.EditorSystemService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.keyWithAnyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static org.springframework.util.StringUtils.hasText;

public class EditorSystemServiceImpl implements EditorSystemService {

  private Repository<NodeId, Node> nodes;
  private UserHelper userHelper;
  
  public EditorSystemServiceImpl(Repository<NodeId, Node> nodes, UserHelper userHelper) {
    this.nodes = nodes;
    this.userHelper = userHelper;
  }
  
  @Override
  public List<System> findAll() {
    return find("", -1);
  }

  @Override
  public List<System> find(String query, int max) {
    List<Criteria> criteria = new ArrayList<>();
    criteria.add(keyValue("type.id", System.TERMED_NODE_CLASS));
    criteria.add(getCurrentUserOrganizationCriteria());

    if(hasText(query)) {
      List<String> tokens = tokenizeAndMap(query, t -> t + "*");
      criteria.add(keyWithAnyValue("properties.prefLabel", tokens));
    }
    
    return this.nodes.query(and(criteria), max)
            .map(System::new)
            .collect(Collectors.toList());
  }

  @Override
  public Optional<System> get(UUID id) {
    
    Optional<System> system = nodes.get(new NodeId(id, System.TERMED_NODE_CLASS))
            .map(System::new);
    
    if(system.isPresent()) {
      checkUserIsAllowedToAccessSystem(system.get());
    }
    
    return system;
  }

  @Override
  public System save(System system) {
    Optional<System> old;

    if (system.getId() == null) {
      old = empty();
      system.setId(randomUUID());
    } else {
      old = get(system.getId());
    }

    if (!old.isPresent()) {
      checkUserIsAllowedToAccessSystem(system);
    }

    system.getLink()
      .ifPresent(l -> l.setId(firstNonNull(l.getId(), randomUUID())));
    
    Changeset<NodeId, Node> changeset;

    if (!old.isPresent()) {
      changeset = changesetForInsert(system);
    }
    else {
      changeset = changesetForUpdate(system, old.get());
    }

    nodes.post(changeset);

    return get(system.getId())
      .orElseThrow(systemNotFoundAfterSave(system.getId()));

  }
  
  private Changeset<NodeId, Node> changesetForInsert(System system) {
    Changeset changeset = Changeset.empty();

    List<Node> save = new ArrayList<>();
    save.add(system.toNode());
    system.getLink().ifPresent(l -> save.add(l.toNode()));
    changeset = changeset.merge(new Changeset(Collections.emptyList(), save));

    return changeset;
  }

  private Changeset<NodeId, Node> changesetForUpdate(System newSystem,
                                                     System oldSystem) {
    Changeset<NodeId, Node> studyChangeset = Changeset.<NodeId, Node>empty()
      .merge(
        buildChangeset(
        newSystem.getLink().orElse(null),
        oldSystem.getLink().orElse(null)));
    studyChangeset = studyChangeset.merge(Changeset.save(newSystem.toNode()));

    return studyChangeset;
  }
  
  private Changeset<NodeId, Node> buildChangeset(Link newLink,
                                                 Link oldLink) {
    if (newLink != null && oldLink == null) {
      return Changeset.save(newLink.toNode());
    }
    // save with existing id
    if (newLink != null && !newLink.equals(oldLink)) {
      return Changeset.save(new Link(
        oldLink.getId(),
        newLink.getPrefLabel(),
        newLink.getLinkUrl()).toNode());
    }
    if (newLink == null && oldLink != null) {
      return Changeset.delete(new NodeId(oldLink.toNode()));
    }
    return Changeset.empty();
  }
  
  private Criteria getCurrentUserOrganizationCriteria() {
    List<String> organizationIds = userHelper.getCurrentUserOrganizations().stream()
      .map(organization -> organization.getId().toString())
      .collect(Collectors.toList());

    return keyWithAnyValue("references.ownerOrganization.id", organizationIds);
  }
  
  private void checkUserIsAllowedToAccessSystem(System system) {
    if (!system.getOwnerOrganization().isPresent()) {
      throwSystemAccessException(system, "which has no organization");
    }

    UUID systemOrganizationId = system.getOwnerOrganization().get().getId();
    Set<UUID> userOrganizationIds = userHelper.getCurrentUserOrganizations()
      .stream()
      .map(org -> org.getId())
      .collect(Collectors.toSet());

    if (!userOrganizationIds.contains(systemOrganizationId)) {
      throwSystemAccessException(system, "because user is not member of system's organization '" + systemOrganizationId + "'");
    }
  }

  private void throwSystemAccessException(System system, String cause) {
    throw new AccessDeniedException(
      new StringBuilder()
        .append("User '")
        .append(userHelper.getCurrentUser().get().getUsername())
        .append("' is not allowed to view/save/delete system '")
        .append(system.getId())
        .append("' ")
        .append(cause)
        .toString()
    );
  }
  
  private Supplier<IllegalStateException> systemNotFoundAfterSave(UUID systemId) {
    return () -> new IllegalStateException("System '" + systemId
      + "' was not found after saving, it might have been updated simultaneously by another user");
  }
  
}
