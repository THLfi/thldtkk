package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valueToEnum;
import static java.util.Objects.requireNonNull;

public class RecipientNotificationState implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "RecipientNotificationState";

  private UUID id;
  private NotificationMessageState notificationState = NotificationMessageState.PENDING;
  private OrganizationPersonInRole personInRole;
  private NotificationSubject subject;

  /**
   * Required by GSON deserialization.
   */
  public RecipientNotificationState() {}

  public RecipientNotificationState(UUID id) {
    this.id = requireNonNull(id);
  }

  public RecipientNotificationState(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));

    this.setNotificationState(valueToEnum(node.getProperties("notificationState"), NotificationMessageState.class));
    this.setSubject(valueToEnum(node.getProperties("subject"), NotificationSubject.class));

    node.getReferencesFirst("personInRole")
      .ifPresent(personInRole -> this.personInRole = new OrganizationPersonInRole(personInRole));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public NotificationMessageState getNotificationState() {
    return notificationState;
  }

  public void setNotificationState(NotificationMessageState notificationState) {
    this.notificationState = notificationState;
  }

  public Optional<NotificationSubject> getSubject() {
    return Optional.ofNullable(subject);
  }

  public void setSubject(NotificationSubject subject) {
    this.subject = subject;
  }

  public OrganizationPersonInRole getPersonInRole() {
    return personInRole;
  }

  public void setPersonInRole(OrganizationPersonInRole personInRole) {
    this.personInRole = personInRole;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.put("notificationState", PropertyMappings.enumToPropertyValue(notificationState));
    props.put("subject", PropertyMappings.enumToPropertyValue(subject));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    if (personInRole != null) {
      refs.put("personInRole", personInRole.toNode());
    }

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
    RecipientNotificationState recipientNotificationState = (RecipientNotificationState) o;
    return Objects.equals(id, recipientNotificationState.id)
      && Objects.equals(notificationState, recipientNotificationState.notificationState)
      && Objects.equals(subject, recipientNotificationState.subject)
      && Objects.equals(personInRole, recipientNotificationState.personInRole);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      notificationState,
      subject,
      personInRole
    );
  }

}
