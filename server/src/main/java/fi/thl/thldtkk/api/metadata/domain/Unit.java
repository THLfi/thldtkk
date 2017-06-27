package fi.thl.thldtkk.api.metadata.domain;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class Unit {

  private static final String TERMED_NODE_CLASS = "Unit";

  private UUID id;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> symbol = new LinkedHashMap<>();

  public Unit(UUID id) {
    this.id = requireNonNull(id);
  }

  public Unit(UUID id, Map<String, String> prefLabel, Map<String, String> symbol) {
    this(id);
    this.prefLabel = prefLabel;
    this.symbol = symbol;
  }

  public Unit(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.symbol = toLangValueMap(node.getProperties("symbol"));
  }

  public UUID getId() {
    return id;
  }

  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Map<String, String> getSymbol() {
    return symbol;
  }

  public Node toNode() {
    Node node = new Node(id, TERMED_NODE_CLASS);
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("symbol", toPropertyValues(symbol));
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
    Unit that = (Unit) o;
    return Objects.equals(id, that.id)
      && Objects.equals(prefLabel, that.prefLabel)
      && Objects.equals(symbol, that.symbol);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, prefLabel, symbol);
  }

}
