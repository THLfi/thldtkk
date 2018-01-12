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

public class Link implements NodeEntity {

    private UUID id;
    private Map<String, String> prefLabel = new LinkedHashMap<>();
    private Map<String, String> linkUrl = new LinkedHashMap<>();

    public Link() {

    }

    public Link(UUID id) {
      this.id = requireNonNull(id);
    }

    public Link(Node node) {
      this(node.getId());
      checkArgument(Objects.equals(node.getTypeId(), "Link"));
      this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
      this.linkUrl = toLangValueMap(node.getProperties("linkUrl"));
    }
    
    public Link(UUID id, Map<String, String> prefLabel, Map<String, String> linkUrl) {
      this.id = requireNonNull(id);
      this.prefLabel = prefLabel;
      this.linkUrl = linkUrl;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getLinkUrl() {
        return linkUrl;
    }

    public Map<String, String> getPrefLabel() {
        return prefLabel;
    }

    public Node toNode() {
        Node node = new Node(id, "Link");
        node.addProperties("prefLabel", toPropertyValues(prefLabel));
        node.addProperties("linkUrl", toPropertyValues(linkUrl));
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
        Link that = (Link) o;
        return Objects.equals(id, that.id)
                && Objects.equals(prefLabel, that.prefLabel)
                && Objects.equals(linkUrl, that.linkUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prefLabel, linkUrl);
    }

}
