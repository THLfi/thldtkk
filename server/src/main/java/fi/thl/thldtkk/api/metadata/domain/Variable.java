package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.UUID;

public class Variable {

    private UUID id;
    @ContainsAtLeastOneNonBlankValue
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();
    private int referenceCount = 0;
    private int refererCount = 0;

    public Variable(UUID id) {
        this.id = requireNonNull(id);
    }

    public Variable(Node node) {
        this(node.getId());
        checkArgument(Objects.equals(node.getTypeId(), "Variable"));
        this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
        this.description = toLangValueMap(node.getProperties("description"));
        this.referenceCount = node.getReferences().size();
        this.refererCount = node.getReferrers().size();
    }

    /**
     * Constructor for testing purposes
     */

    public Variable(UUID id, Map<String, String> prefLabel, Map<String, String> description) {
      this.id = id;
      this.prefLabel = prefLabel;
      this.description = description;
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

    public int getReferenceCount() { return referenceCount; }

    public int getRefererCount() { return refererCount; }

    public Node toNode() {
        Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
        props.putAll("prefLabel", toPropertyValues(prefLabel));
        props.putAll("description", toPropertyValues(description));
        return new Node(id, "Variable", props);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Variable that = (Variable) o;
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, description);
    }

}
