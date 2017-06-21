package fi.thl.thldtkk.api.metadata.domain;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class CodeItem {

  private static final String TERMED_NODE_CLASS = "CodeItem";

  private UUID id;
  private String code;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public CodeItem(UUID id) {
    this.id = requireNonNull(id);
  }

  public CodeItem(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.code = PropertyMappings.toString(node.getProperties("code"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<String> getCode() {
    return Optional.ofNullable(code);
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Node toNode() {
    Node node = new Node(id, TERMED_NODE_CLASS);
    getCode().ifPresent(rid -> node.getProperties().put("code", toPropertyValue(rid)));
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    return node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodeItem that = (CodeItem) o;
    return Objects.equals(id, that.id)
      && Objects.equals(code, that.code)
      && Objects.equals(prefLabel, that.prefLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, code);
  }

}
