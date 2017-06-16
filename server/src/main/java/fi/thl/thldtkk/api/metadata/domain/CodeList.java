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

public class CodeList {

  private static final String TERMED_NODE_CLASS = "CodeList";

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Map<String, String> owner = new LinkedHashMap<>();
  private String referenceId;

  public CodeList(UUID id) {
    this.id = requireNonNull(id);
  }

  public CodeList(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.owner = toLangValueMap(node.getProperties("owner"));
    this.referenceId = PropertyMappings.toString(node.getProperties("referenceId"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getDescription() {
    return description;
  }

  public Map<String, String> getOwner() {
    return owner;
  }

  public Optional<String> getReferenceId() {
    return Optional.ofNullable(referenceId);
  }

  public Node toNode() {
    Node node = new Node(id, TERMED_NODE_CLASS);
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("description", toPropertyValues(description));
    node.addProperties("owner", toPropertyValues(owner));
    getReferenceId().ifPresent(rid -> node.getProperties().put("referenceId", toPropertyValue(rid)));
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
    CodeList that = (CodeList) o;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel)
      && Objects.equals(description, that.description)
      && Objects.equals(owner, that.owner)
      && Objects.equals(referenceId, that.referenceId);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, prefLabel, description, owner, referenceId);
  }

}
