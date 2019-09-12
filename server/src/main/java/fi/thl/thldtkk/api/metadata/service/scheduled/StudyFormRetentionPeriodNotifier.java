package fi.thl.thldtkk.api.metadata.service.scheduled;

import static fi.thl.thldtkk.api.metadata.domain.query.AndCriteria.and;
import static fi.thl.thldtkk.api.metadata.domain.query.KeyValueCriteria.keyValue;
import static java.util.Arrays.asList;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.thl.thldtkk.api.metadata.domain.NotificationSubject;
import fi.thl.thldtkk.api.metadata.domain.NotificationMessageState;
import fi.thl.thldtkk.api.metadata.domain.Organization;
import fi.thl.thldtkk.api.metadata.domain.OrganizationPersonInRole;
import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.RecipientNotificationState;
import fi.thl.thldtkk.api.metadata.domain.Role;
import fi.thl.thldtkk.api.metadata.domain.RoleLabel;
import fi.thl.thldtkk.api.metadata.domain.Study;
import fi.thl.thldtkk.api.metadata.domain.StudyForm;
import fi.thl.thldtkk.api.metadata.domain.StudyFormConfirmationState;
import fi.thl.thldtkk.api.metadata.domain.query.Select;
import fi.thl.thldtkk.api.metadata.domain.termed.Changeset;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.NodeId;
import fi.thl.thldtkk.api.metadata.service.EmailService;
import fi.thl.thldtkk.api.metadata.service.Repository;
import fi.thl.thldtkk.api.metadata.service.email.StudyFormEmailTemplateFactory;

@Component
public class StudyFormRetentionPeriodNotifier {

  @Autowired
  @Qualifier("editor-node-repository")
  private Repository<NodeId, Node> nodes;

  @Autowired
  private EmailService emailService;

  @Autowired
  private StudyFormEmailTemplateFactory emailTemplateFactory;

  /**
   * Poll termed for all studies, scan them for expired retentionPeriods, and
   * send notification emails when appropriate.
   * 
   * NOTE: Interval should be reasonably long due to query being quite
   * demanding. If additional performance is required, there should be room for
   * improvement by adding a termed native WHERE clause to only pick studyforms
   * with expired retention periods
   *
   */
  @Scheduled(fixedDelayString = "${scheduledTasks.interval}", initialDelay = 5000)
  public void pollForExpiredRetentionPeriods() {
    nodes.query(
      new Select(asList(
          "id",
          "type",
          "lastModifiedDate",
          "properties.*",
          "references.studyForms",
          "references.ownerOrganization",
          "references.personInRoles:2",
          "references.unitInCharge:3",
          "references.notificationStates:3",
          "references.personInRole:4",
          "references.person:5",
          "references.role:5"
        )), and(keyValue("type.id", Study.TERMED_NODE_CLASS)))
      .map(Study::new)
      .forEach(this::checkStudyForExpiration);
  }

  private void checkStudyForExpiration(Study study) {
    List<StudyForm> expiredStudyForms =
      study.getStudyForms().stream()
        .filter(retentionPeriodIsConfirmed())
        .filter(retentionPeriodIsExpired())
        .collect(Collectors.toList());

    expiredStudyForms.stream()
      .filter(unitInChargeIsConfirmed())
      .forEach(studyForm -> sendMailToHeadOfUnit(study, studyForm));

    expiredStudyForms.stream()
      .forEach(studyForm -> sendMailToHeadOfSampleManagement(study, studyForm));
  }


  private void sendMailToHeadOfUnit(Study study, StudyForm studyForm) {
    Predicate<OrganizationPersonInRole> personhasNotBeenNotified = personHasBeenNotifiedBy(studyForm.getNotificationStates()).negate();
    getPersonsInRoleFromUnitInCharge(studyForm, RoleLabel.HEAD_OF_ORGANIZATION).stream()
      .filter(personhasNotBeenNotified)
      .forEach(headOfUnit -> {
        SimpleMailMessage mail = emailTemplateFactory.makeRetentionPeriodExpirationMessage(study);
        emailService.sendEmail(headOfUnit.getPerson(), mail);

        RecipientNotificationState newNotificationState = new RecipientNotificationState(UUID.randomUUID());
        newNotificationState.setSubject(NotificationSubject.STUDY_FORM_EXPIRATION);
        newNotificationState.setPersonInRole(headOfUnit);
        newNotificationState.setNotificationState(NotificationMessageState.SENT);
        studyForm.getNotificationStates().add(newNotificationState);

        List<Node> changedNodes = Arrays.asList(newNotificationState.toNode(), studyForm.toNode());
        Changeset<NodeId, Node> changeset = new Changeset<>(Collections.emptyList(), changedNodes);

        nodes.post(changeset);
      });
  }

  private void sendMailToHeadOfSampleManagement(Study study, StudyForm studyForm) {
    Predicate<OrganizationPersonInRole> personhasNotBeenNotified = personHasBeenNotifiedBy(studyForm.getNotificationStates()).negate();
    getPersonsInRoleFromOrganization(study, RoleLabel.HEAD_OF_SAMPLE_MANAGEMENT).stream()
      .filter(personhasNotBeenNotified)
      .forEach(headOfSampleManagement -> {
        SimpleMailMessage mail = emailTemplateFactory.makeRetentionPeriodExpirationMessage(study);
        emailService.sendEmail(headOfSampleManagement.getPerson(), mail);

        RecipientNotificationState newNotificationState = new RecipientNotificationState(UUID.randomUUID());
        newNotificationState.setSubject(NotificationSubject.STUDY_FORM_EXPIRATION);
        newNotificationState.setPersonInRole(headOfSampleManagement);
        newNotificationState.setNotificationState(NotificationMessageState.SENT);
        studyForm.getNotificationStates().add(newNotificationState);

        Changeset<NodeId, Node> changeset = Changeset.<NodeId, Node>save(newNotificationState.toNode())
          .merge(Changeset.save(studyForm.toNode()));

        nodes.post(changeset);
      });
  }

  private static Predicate<StudyForm> retentionPeriodIsExpired() {
    return studyForm ->
      studyForm.getRetentionPeriod().isPresent()
        && LocalDate.now().isAfter(studyForm.getRetentionPeriod().get());
  }

  private static Predicate<StudyForm> retentionPeriodIsConfirmed() {
    return studyForm ->
      Objects.equals(studyForm.getRetentionPeriodConfirmationState().orElse(null), StudyFormConfirmationState.ACCEPTED);
  }

  private static Predicate<StudyForm> unitInChargeIsConfirmed() {
    return studyForm ->
      studyForm.getUnitInCharge().isPresent() &&
        Objects.equals(studyForm.getUnitInChargeConfirmationState().orElse(null), StudyFormConfirmationState.ACCEPTED);
  }

  private static Predicate<OrganizationPersonInRole> personHasBeenNotifiedBy(List<RecipientNotificationState> givenNotifications) {
    return personInRole ->
      givenNotifications.stream()
        .anyMatch(notificationState -> {
          if (Objects.equals(notificationState.getPersonInRole().getId(), personInRole.getId())) {
            return Objects.equals(notificationState.getNotificationState(), NotificationMessageState.SENT)
              && Objects.equals(notificationState.getSubject().orElse(null), NotificationSubject.STUDY_FORM_EXPIRATION);
          } else {
            return false;
          }
        });
  }

  private static Predicate<StudyForm> notificationSentToRole(RoleLabel roleLabel) {
    return studyForm ->
      studyForm.getNotificationStates().stream()
        .anyMatch(notificationState -> {
          if (notificationRecipientHasRoleLabel(notificationState, roleLabel)) {
            return Objects.equals(notificationState.getNotificationState(), NotificationMessageState.SENT);
          } else {
            return false;
          }
        });
  }

  private static boolean notificationRecipientHasRoleLabel(RecipientNotificationState notificationState, RoleLabel label) {
    return Optional.of(notificationState)
      .map(RecipientNotificationState::getPersonInRole)
      .filter(StudyFormRetentionPeriodNotifier.personHasRoleLabel(label))
      .isPresent();
  }

  private static List<OrganizationPersonInRole> getPersonsInRoleFromUnitInCharge(StudyForm studyForm, RoleLabel label) {
    return Optional.of(studyForm)
      .flatMap(StudyForm::getUnitInCharge)
      .map(OrganizationUnit::getPersonInRoles)
      .map(Collection::stream)
      .orElse(Stream.empty())
      .filter(personHasRoleLabel(label))
      .collect(Collectors.toList());
  }

  private static List<OrganizationPersonInRole> getPersonsInRoleFromOrganization(Study study, RoleLabel label) {
    return Optional.of(study)
      .flatMap(Study::getOwnerOrganization)
      .map(Organization::getPersonInRoles)
      .map(Collection::stream)
      .orElse(Stream.empty())
      .filter(personHasRoleLabel(label))
      .collect(Collectors.toList());
  }

  private static Predicate<OrganizationPersonInRole> personHasRoleLabel(RoleLabel label) {
    return personInRole -> {
      Optional<RoleLabel> recipientLabel = 
        Optional.of(personInRole)
          .map(OrganizationPersonInRole::getRole)
          .map(Role::getLabel);

      if (recipientLabel.isPresent()) {
        return Objects.equals(recipientLabel.get(), label);
      } else {
        return false;
      }
    };
  }
}