package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;
import fi.thl.thldtkk.api.metadata.validator.ContainsAtLeastOneNonBlankValue;
import fi.thl.thldtkk.api.metadata.validator.NotNullAndHasId;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static java.util.Objects.requireNonNull;

public class StudyGroup implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "StudyGroup";

  private UUID id;
  private Date lastModifiedDate;
  private UserProfile lastModifiedByUser;
  @ContainsAtLeastOneNonBlankValue
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private LocalDate referencePeriodStart;
  private LocalDate referencePeriodEnd;
  @NotNullAndHasId
  private Organization ownerOrganization;

  /**
   * Required by GSON deserialization.
   */
  private StudyGroup() {

  }

  public StudyGroup(UUID id) {
    this.id = requireNonNull(id);
  }

  public StudyGroup(Node node) {
    this(node.getId());

    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));

    this.lastModifiedDate = node.getLastModifiedDate();

    this.prefLabel = toLangValueMap(node.getProperties("prefLabel"));
    this.description = toLangValueMap(node.getProperties("description"));
    this.referencePeriodStart = toLocalDate(node.getProperties("referencePeriodStart"), null);
    this.referencePeriodEnd = toLocalDate(node.getProperties("referencePeriodEnd"), null);

    node.getReferencesFirst("lastModifiedByUser")
        .ifPresent(up -> this.lastModifiedByUser = new UserInformation(new UserProfile(up)));
    node.getReferencesFirst("ownerOrganization")
      .ifPresent(oo -> this.ownerOrganization = new Organization(oo));
  }

  /**
   * Constructor for testing purposes. Add attributes if needed.
   */
  public StudyGroup(UUID id,
                    Map<String, String> prefLabel) {
    this.id = id;
    this.prefLabel = prefLabel;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<Date> getLastModifiedDate() {
    return Optional.ofNullable(lastModifiedDate);
  }

  public Optional<UserProfile> getLastModifiedByUser() {
    return Optional.ofNullable(lastModifiedByUser);
  }

  public void setLastModifiedByUser(UserProfile userProfile) {
    this.lastModifiedByUser = userProfile;
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

  public Optional<Organization> getOwnerOrganization() {
    return Optional.ofNullable(ownerOrganization);
  }

  /**
   * Transforms dataset into node
   */
  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.putAll("prefLabel", toPropertyValues(prefLabel));
    props.putAll("description", toPropertyValues(description));
    getReferencePeriodStart().ifPresent(v -> props.put("referencePeriodStart", toPropertyValue(v)));
    getReferencePeriodEnd().ifPresent(v -> props.put("referencePeriodEnd", toPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getLastModifiedByUser().ifPresent(v -> refs.put("lastModifiedByUser", v.toNode()));
    getOwnerOrganization().ifPresent(oo -> refs.put("ownerOrganization", oo.toNode()));

    return new Node(id, TERMED_NODE_CLASS, props, refs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StudyGroup study = (StudyGroup) o;
    return Objects.equals(id, study.id)
      && Objects.equals(lastModifiedDate, study.lastModifiedDate)
      && Objects.equals(lastModifiedByUser, study.lastModifiedByUser)
      && Objects.equals(prefLabel, study.prefLabel)
      && Objects.equals(description, study.description)
      && Objects.equals(referencePeriodStart, study.referencePeriodStart)
      && Objects.equals(referencePeriodEnd, study.referencePeriodEnd)
      && Objects.equals(ownerOrganization, study.ownerOrganization);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      lastModifiedDate,
      lastModifiedByUser,
      prefLabel,
      description,
      referencePeriodStart,
      referencePeriodEnd,
      ownerOrganization);
  }

}
