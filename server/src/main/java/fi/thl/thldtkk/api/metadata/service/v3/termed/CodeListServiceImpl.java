package fi.thl.thldtkk.api.metadata.service.v3.termed;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.Maps.difference;
import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.CriteriaUtils.anyKeyWithAllValues;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static fi.thl.thldtkk.api.metadata.domain.query.Select.select;
import static fi.thl.thldtkk.api.metadata.domain.query.Sort.sort;
import static fi.thl.thldtkk.api.metadata.util.MapUtils.index;
import static fi.thl.thldtkk.api.metadata.util.Tokenizer.tokenizeAndMap;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.v3.CodeListService;
import fi.thl.thldtkk.api.metadata.service.v3.Repository;
import fi.thl.thldtkk.api.metadata.util.spring.exception.NotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CodeListServiceImpl implements CodeListService {

  private Repository<NodeId, Node> nodes;

  public CodeListServiceImpl(Repository<NodeId, Node> nodes) {
    this.nodes = nodes;
  }

  @Override
  public List<CodeList> findAll() {
    return nodes.query(
        select("id", "type", "properties.*", "references.*"),
        keyValue("type.id", "CodeList"),
        sort("properties.prefLabel.sortable"))
        .map(CodeList::new)
        .collect(toList());
  }

  @Override
  public List<CodeList> find(String query, int max) {
    return nodes.query(
        select("id", "type", "properties.*", "references.*"),
        and(keyValue("type.id", "CodeList"),
            anyKeyWithAllValues(asList(
                "properties.prefLabel",
                "properties.owner",
                "properties.referenceId"),
                tokenizeAndMap(query, t -> t + "*"))),
        sort("properties.prefLabel.sortable"),
        max)
        .map(CodeList::new)
        .collect(toList());
  }

  @Override
  public List<CodeList> findByExactPrefLabel(String prefLabel, int max) {
    return findByExactProperty("prefLabel", prefLabel, max);
  }

  private List<CodeList> findByExactProperty(String property, String value, int max) {
    return nodes.query(
      select("id", "type", "properties.*", "references.*"),
      and(
        keyValue("type.id", "CodeList"),
        keyValue("properties." + property, "\"" + value + "\"")
      ),
      sort("properties.prefLabel.sortable"),
      max)
      .map(CodeList::new)
      .collect(toList());
  }

  @Override
  public List<CodeList> findByExactReferenceId(String referenceId, int max) {
    return findByExactProperty("referenceId", referenceId, max);
  }

  @Override
  public Optional<CodeList> get(UUID id) {
    return nodes.get(new NodeId(id, "CodeList")).map(CodeList::new);
  }

  @Override
  public CodeList save(CodeList codeList) {
    Optional<CodeList> existingCodeList;
    if (codeList.getId() == null) {
      existingCodeList = empty();
      codeList.setId(randomUUID());
    } else {
      existingCodeList = get(codeList.getId());
    }

    if (CodeList.CODE_LIST_TYPE_EXTERNAL.equals(codeList.getCodeListType().orElseGet(null))) {
      codeList.getCodeItems().clear();
    } else if (CodeList.CODE_LIST_TYPE_INTERNAL
        .equals(codeList.getCodeListType().orElseGet(null))) {
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
    nodes.post(changeset);
  }

  private void insert(CodeList codeList) {
    List<Node> nodesToSave = new LinkedList<>();
    nodesToSave.add(codeList.toNode());
    codeList.getCodeItems().forEach(ci -> nodesToSave.add(ci.toNode()));
    nodes.save(nodesToSave);
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

    nodes.delete(nodesToDelete.stream().map(NodeId::new).collect(toList()));
  }

}
