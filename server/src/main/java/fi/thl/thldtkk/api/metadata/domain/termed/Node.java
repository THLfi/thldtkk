package fi.thl.thldtkk.api.metadata.domain.termed;

import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class  Node {

  private UUID id;
  private TypeId type;

  private String code;
  private String uri;

  private String createdBy;
  private Date createdDate;
  private String lastModifiedBy;
  private Date lastModifiedDate;

  private Multimap<String, StrictLangValue> properties = LinkedHashMultimap.create();
  private Multimap<String, Node> references = LinkedHashMultimap.create();
  private Multimap<String, Node> referrers = LinkedHashMultimap.create();

  public Node() {
  }

  public Node(UUID id) {
    this.id = id;
  }

  public Node(UUID id, String typeId) {
    this.id = id;
    this.type = new TypeId(typeId);
  }

  public Node(UUID id, String typeId, Multimap<String, StrictLangValue> properties) {
    this.id = id;
    this.type = new TypeId(typeId);
    this.properties = properties;
  }

  public Node(UUID id, String typeId,
    Multimap<String, StrictLangValue> properties,
    Multimap<String, Node> references) {
    this.id = id;
    this.type = new TypeId(typeId);
    this.properties = properties;
    this.references = references;
  }

  public Node(UUID id, String typeId,
    Multimap<String, StrictLangValue> properties,
    Multimap<String, Node> references,
    Multimap<String, Node> referrers) {
    this.id = id;
    this.type = new TypeId(typeId);
    this.properties = properties;
    this.references = references;
    this.referrers = referrers;
  }

  public UUID getId() {
    return id;
  }

  public TypeId getType() {
    return type;
  }

  public String getTypeId() {
    return type != null ? type.getId() : null;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Multimap<String, StrictLangValue> getProperties() {
    return properties;
  }

  public void setProperties(Multimap<String, StrictLangValue> properties) {
    this.properties = properties;
  }

  public Collection<StrictLangValue> getProperties(String propertyKey) {
    return properties.get(propertyKey);
  }

  public void addProperties(String property, Iterable<StrictLangValue> values) {
    properties.putAll(property, values);
  }

  public Multimap<String, Node> getReferences() {
    return references;
  }

  public void setReferences(Multimap<String, Node> references) {
    this.references = references;
  }

  public Collection<Node> getReferences(String referenceKey) {
    return references.get(referenceKey);
  }

  public Optional<Node> getReferencesFirst(String referenceKey) {
    return references.get(referenceKey).stream().findFirst();
  }

  public void addReference(String property, Node value) {
    references.put(property, value);
  }

  public Multimap<String, Node> getReferrers() {
    return referrers;
  }

  public void setReferrers(Multimap<String, Node> referrers) {
    this.referrers = referrers;
  }

  public Collection<Node> getReferrers(String referrerKey) {
    return referrers.get(referrerKey);
  }

  public Optional<Node> getReferrersFirst(String referrerKey) {
    return referrers.get(referrerKey).stream().findFirst();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .add("type", type)
      .add("code", code)
      .add("uri", uri)
      .add("createdBy", createdBy)
      .add("createdDate", createdDate)
      .add("lastModifiedBy", lastModifiedBy)
      .add("lastModifiedDate", lastModifiedDate)
      .add("properties", properties)
      .add("references", references)
      .add("referrers", referrers)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Node node = (Node) o;
    return Objects.equals(id, node.id) &&
      Objects.equals(type, node.type) &&
      Objects.equals(code, node.code) &&
      Objects.equals(uri, node.uri) &&
      Objects.equals(createdBy, node.createdBy) &&
      Objects.equals(createdDate, node.createdDate) &&
      Objects.equals(lastModifiedBy, node.lastModifiedBy) &&
      Objects.equals(lastModifiedDate, node.lastModifiedDate) &&
      Objects.equals(properties, node.properties) &&
      Objects.equals(references, node.references);
      // 'referrers' is left out on purpose because it is supposed to be transient
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, code, uri, createdBy, createdDate, lastModifiedBy,
      lastModifiedDate, properties, references, referrers);
  }

}
