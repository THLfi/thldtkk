package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toSingleInteger;
import static java.util.Objects.requireNonNull;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Population {

  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Integer sampleSize;
  private Integer loss;
  private Map<String, String> geographicalCoverage = new LinkedHashMap<>();

  public Population(UUID id) {
    this.id = requireNonNull(id);
  }

  public Population(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), "Population"));
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.sampleSize = toSingleInteger(node.getProperties("sampleSize"), null);
    this.loss = toSingleInteger(node.getProperties("loss"), null);
    this.geographicalCoverage = toLangValueMap(node.getProperties("geographicalCoverage"));
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

  public Optional<Integer> getSampleSize() {
    return Optional.ofNullable(sampleSize);
  }

  public Optional<Integer> getLoss() {
    return Optional.ofNullable(loss);
  }

  public Map<String, String> getGeographicalCoverage() {
    return geographicalCoverage;
  }

  public Node toNode() {
    Node node = new Node(id, "Population");
    node.addProperties("prefLabel", toPropertyValues(prefLabel));
    node.addProperties("description", toPropertyValues(description));
    getSampleSize().ifPresent(v -> node.addProperties("sampleSize", toPropertyValues(v)));
    getLoss().ifPresent(v -> node.addProperties("loss", toPropertyValues(v)));
    node.addProperties("geographicalCoverage", toPropertyValues(geographicalCoverage));
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
    Population that = (Population) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(prefLabel, that.prefLabel) &&
        Objects.equals(description, that.description) &&
        Objects.equals(sampleSize, that.sampleSize) &&
        Objects.equals(loss, that.loss) &&
        Objects.equals(geographicalCoverage, that.geographicalCoverage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, description, sampleSize, loss, geographicalCoverage);
  }
}
