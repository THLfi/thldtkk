package fi.thl.thldtkk.api.metadata.domain;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import java.util.UUID;

public class InstanceVariable {

    private UUID id;
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> description = new LinkedHashMap<>();
    private LocalDate referencePeriodStart;
    private LocalDate referencePeriodEnd;
    private String technicalName;
    private List<Concept> conceptsFromScheme = new ArrayList<>();

    public InstanceVariable(UUID id) {
        this.id = requireNonNull(id);
    }

    public InstanceVariable(Node node) {
        this(node.getId());
        checkArgument(Objects.equals(node.getTypeId(), "InstanceVariable"));
        this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
        this.description = toLangValueMap(node.getProperties("description"));
        this.referencePeriodStart = toLocalDate(node.getProperties("referencePeriodStart"), null);
        this.referencePeriodEnd = toLocalDate(node.getProperties("referencePeriodEnd"), null);
        this.technicalName = PropertyMappings.toString(node.getProperties("technicalName"));
        node.getReferences("conceptsFromScheme")
          .forEach(c -> this.conceptsFromScheme.add(new Concept(c)));
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

    public Optional<LocalDate> getReferencePeriodStart() {
        return Optional.ofNullable(referencePeriodStart);
    }

    public Optional<LocalDate> getReferencePeriodEnd() {
        return Optional.ofNullable(referencePeriodEnd);
    }

    public Optional<String> getTechnicalName() {
        return Optional.ofNullable(technicalName);
    }

    public List<Concept> getConceptsFromScheme() {
      return conceptsFromScheme;
    }

    public Node toNode() {
        Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
        props.putAll("prefLabel", toPropertyValues(prefLabel));
        props.putAll("description", toPropertyValues(description));
        getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
        getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));
        getTechnicalName().ifPresent((v -> props.put("technicalName", toPropertyValue(v))));

        Multimap<String, Node> refs = LinkedHashMultimap.create();
        getConceptsFromScheme().forEach(c -> refs.put("conceptsFromScheme", c.toNode()));

        return new Node(id, "InstanceVariable", props, refs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InstanceVariable that = (InstanceVariable) o;
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(description, that.description)
                && Objects.equals(referencePeriodStart, that.referencePeriodStart)
                && Objects.equals(referencePeriodEnd, that.referencePeriodEnd)
                && Objects.equals(technicalName, that.technicalName)
                && Objects.equals(conceptsFromScheme, that.conceptsFromScheme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, description, referencePeriodStart,
                referencePeriodEnd, technicalName, conceptsFromScheme);
    }

}
