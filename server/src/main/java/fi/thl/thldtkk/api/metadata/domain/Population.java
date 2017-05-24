package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.UUID;

public class Population {

    private UUID id;
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();
    private Map<String, String> sampleSize = new LinkedHashMap<>();
    private Map<String, String> loss = new LinkedHashMap<>();
    private Map<String, String> geographicalCoverage = new LinkedHashMap<>();

    public Population(UUID id) {
        this.id = requireNonNull(id);
    }

    public Population(UUID id,
            Map<String, String> prefLabel,
            Map<String, String> description,
            Map<String, String> sampleSize,
            Map<String, String> loss,
            Map<String, String> geographicalCoverage) {
        this(id);
        this.prefLabel = prefLabel;
        this.description = description;
        this.sampleSize = sampleSize;
        this.loss = loss;
        this.geographicalCoverage = geographicalCoverage;
    }

    public Population(Node node) {
        this(node.getId());
        checkArgument(Objects.equals(node.getTypeId(), "Population"));
        this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
        this.description = toLangValueMap(node.getProperties("description"));
        this.sampleSize = toLangValueMap(node.getProperties("sampleSize"));
        this.loss = toLangValueMap(node.getProperties("loss"));
        this.geographicalCoverage = toLangValueMap(node.getProperties("geographicalCoverage"));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getPrefLabel() {
        return prefLabel;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public Map<String, String> getSampleSize() {
        return sampleSize;
    }

    public Map<String, String> getLoss() {
        return loss;
    }

    public Map<String, String> getGeographicalCoverage() {
        return geographicalCoverage;
    }

    public Node toNode() {
        Node node = new Node(id, "Population");
        node.addProperties("prefLabel", toPropertyValues(prefLabel));
        node.addProperties("description", toPropertyValues(description));
        node.addProperties("sampleSize", toPropertyValues(sampleSize));
        node.addProperties("loss", toPropertyValues(loss));
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
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(description, that.description)
                && Objects.equals(sampleSize, that.sampleSize)
                && Objects.equals(loss, that.loss)
                && Objects.equals(geographicalCoverage, that.geographicalCoverage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, description, sampleSize, loss, geographicalCoverage);
    }
}
