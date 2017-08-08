package fi.thl.thldtkk.api.metadata.domain;

import fi.thl.thldtkk.api.metadata.domain.termed.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class Universe {

    private static final String TERMED_NODE_CLASS = "Universe";

    private UUID id;
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();

    public Universe(UUID id) {
        this.id = requireNonNull(id);
    }

    public Universe(UUID id,
                    Map<String, String> prefLabel,
                    Map<String, String> description) {
        this(id);
        this.prefLabel = prefLabel;
        this.description = description;
    }

    public Universe(Node node) {
        this(node.getId());
        checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
        this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
        this.description = toLangValueMap(node.getProperties("description"));
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

    public Node toNode() {
        Node node = new Node(id, TERMED_NODE_CLASS);
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
        Universe that = (Universe) o;
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, description);
    }

}
