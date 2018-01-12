package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class System implements NodeEntity {
  
  public static final String TERMED_NODE_CLASS = "System";
  
  private UUID id;

  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Link link;
  @NotNull
  private Organization ownerOrganization;

  public System(UUID id) {
    this.id = requireNonNull(id);
  }
  
  public System(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    
    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    node.getReferencesFirst("link").ifPresent(l -> this.link
            = new Link(l));
    node.getReferencesFirst("ownerOrganization").ifPresent(o -> this.ownerOrganization
            = new Organization(o));
  }

  @Override
  public UUID getId() {
    return this.id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
  
  public Map<String, String> getPrefLabel() {
    return prefLabel;
  }

  public Optional<Link> getLink() {
    return Optional.ofNullable(link);
  }

  public Optional<Organization> getOwnerOrganization() {
    return Optional.ofNullable(ownerOrganization);
  }
  
  @Override
  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getOwnerOrganization().ifPresent(o -> refs.put("ownerOrganization", o.toNode()));
    getLink().ifPresent(l -> refs.put("link", l.toNode()));

    return new Node(id, TERMED_NODE_CLASS, props, refs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prefLabel, ownerOrganization, link);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    
    final System other = (System) obj;
    
    return Objects.equals(id, other.id)
      && Objects.equals(prefLabel, other.prefLabel)
      && Objects.equals(ownerOrganization, other.ownerOrganization)
      && Objects.equals(link, other.link);
  }
}
