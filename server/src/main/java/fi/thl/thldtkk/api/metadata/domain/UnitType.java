package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.UUID;

public class UnitType {

    private UUID id;
    @ContainsAtLeastOneNonBlankValue
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();

    public UnitType() {

    }

    public UnitType(UUID id) {
        this.id = requireNonNull(id);
    }

    public UnitType(Node node) {
        this(node.getId());
        checkArgument(Objects.equals(node.getTypeId(), "UnitType"));
        this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
        this.description = toLangValueMap(node.getProperties("description"));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public Map<String, String> getPrefLabel() {
        return prefLabel;
    }

    public Node toNode() {
        Node node = new Node(id, "UnitType");
        node.addProperties("prefLabel", toPropertyValues(prefLabel));
        node.addProperties("description", toPropertyValues(description));
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
        UnitType that = (UnitType) o;
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, description);
    }

}
