package fi.thl.thldtkk.api.metadata.service;

import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Maps.difference;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Component
public class CodeListService implements Service<UUID, CodeList> {

  private TermedNodeService nodeService;

  @Autowired
  public CodeListService(TermedNodeService nodeService) {
    this.nodeService = nodeService;
  }

  @Override
  public Stream<CodeList> query() {
    return query("");
  }

  @Override
  public Stream<CodeList> query(String query) {
    return query(query, -1);
  }

  public Stream<CodeList> query(String query, int maxResults) {
    List<String> byPrefLabel = stream(query.split("\\s"))
      .map(token -> "properties.prefLabel:" + token + "*")
      .collect(toList());
    List<String> byOwner = stream(query.split("\\s"))
      .map(token -> "properties.owner:" + token + "*")
      .collect(toList());

    StringBuilder queryBuilder = new StringBuilder();
    queryBuilder.append("type.id:CodeList AND ((");
    queryBuilder.append(String.join(" AND ", byPrefLabel));
    queryBuilder.append(") OR (");
    queryBuilder.append(String.join(" AND ", byOwner));
    queryBuilder.append("))");

    return nodeService.query(
      new NodeRequestBuilder()
        .withQuery(queryBuilder.toString())
        .addDefaultIncludedAttributes()
        .addIncludedAttribute("references.*")
        .addSort("prefLabel")
        .withMaxResults(maxResults)
        .build()
    ).map(CodeList::new);
  }

  @Override
  public Optional<CodeList> get(UUID id) {
    return nodeService.get(new NodeId(id, "CodeList")).map(CodeList::new);
  }

  @Override
  public CodeList save(CodeList codeList) {
    Optional<CodeList> existingCodeList;
    if (codeList.getId() == null ) {
      existingCodeList = empty();
      codeList.setId(randomUUID());
    }
    else {
      existingCodeList = get(codeList.getId());
    }

    if (CodeList.CODE_LIST_TYPE_EXTERNAL.equals(codeList.getCodeListType().orElseGet(null))) {
      codeList.getCodeItems().clear();
    }
    else if (CodeList.CODE_LIST_TYPE_INTERNAL.equals(codeList.getCodeListType().orElseGet(null))) {
      codeList.setReferenceId(null);
    }

    codeList.getCodeItems().forEach(ci -> ci.setId(firstNonNull(ci.getId(), randomUUID())));

    if (existingCodeList.isPresent()) {
      update(codeList, existingCodeList.get());
    } else {
      insert(codeList);
    }

    return get(codeList.getId()).orElseThrow(IllegalStateException::new);
  }

  private void update(CodeList newCodeList, CodeList existingCodeList) {
    Changeset<NodeId, Node> changeset = Changeset.<NodeId, Node>save(
      newCodeList.toNode())
      .merge(buildChangeset(
        newCodeList.getCodeItems(),
        existingCodeList.getCodeItems()));
    nodeService.post(changeset);
  }

  private void insert(CodeList codeList) {
    List<Node> nodesToSave = new LinkedList<>();
    nodesToSave.add(codeList.toNode());
    codeList.getCodeItems().forEach(ci -> nodesToSave.add(ci.toNode()));
    nodeService.save(nodesToSave);
  }

  private Changeset<NodeId, Node> buildChangeset(
    List<CodeItem> newCodeItems,
    List<CodeItem> oldCodeItems) {

    Map<UUID, CodeItem> newCodeItemsById = index(newCodeItems, CodeItem::getId);
    Map<UUID, CodeItem> oldCodeItemsById = index(oldCodeItems, CodeItem::getId);

    List<NodeId> deleted = difference(newCodeItemsById, oldCodeItemsById)
      .entriesOnlyOnRight()
      .values()
      .stream()
      .map(CodeItem::toNode)
      .map(NodeId::new)
      .collect(toList());

    List<Node> saved = newCodeItems.stream()
      .map(CodeItem::toNode).collect(toList());

    return new Changeset<>(deleted, saved);
  }

  @Override
  public void delete(UUID id) {
    List<Node> nodesToDelete = new LinkedList<>();

    CodeList codeList = get(id).orElseThrow(NotFoundException::new);
    nodesToDelete.add(codeList.toNode());

    codeList.getCodeItems().forEach(ci -> nodesToDelete.add(ci.toNode()));

    nodeService.delete(nodesToDelete.stream().map(NodeId::new).collect(toList()));
  }

}
