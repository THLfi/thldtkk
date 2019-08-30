package fi.thl.thldtkk.api.metadata.service.email;

import fi.thl.thldtkk.api.metadata.domain.*;
import fi.thl.thldtkk.api.metadata.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailServiceImpl implements EmailService {

  @Autowired
  public JavaMailSender emailSender;

  @Value("${spring.mail.from}")
  private String from;

  public void sendUnitInChargeConfirmationMessage(Study study, OrganizationUnit unit) {
    List<Person> headsOfOrganization = getHeadsOfOrganization(unit);

    String subject = "Aineiston vastuutus";
    String text =
      "Sinun yksikkösi on merkitty vastuuseen aineiston " +
      study.getPrefLabel().get("fi") +
      " olomuodosta. Käy hyväksymässä vastuutus: " +
      ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
      "/editor/studies/" +
      study.getId() +
      "/edit-administrative-information";

      sendEmails(headsOfOrganization, text, subject);
  }

  public void sendRetentionPeriodConfirmationMessage(Study study, OrganizationUnit unit) {
    List<Person> headsOfOrganization = getHeadsOfOrganization(unit);

    String subject = "Aineiston säilytysajan hyväksyminen";
    String text =
      "Sinun yksikkösi on merkitty vastuuseen aineiston " +
      study.getPrefLabel().get("fi") +
      " olomuodosta. Aineiston säilytysaikaa on muutettu ja muutos on hyväksyttyvä osoitteessa: " +
      ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() +
      "/editor/studies/" +
      study.getId() +
      "/edit-administrative-information";

      sendEmails(headsOfOrganization, text, subject);
  }

  private void sendEmails(List<Person> recipients, String content, String subject) {
    recipients.forEach(headOfOrganization -> {
      SimpleMailMessage message = new SimpleMailMessage();
      //noinspection OptionalGetWithoutIsPresent
      message.setTo(headOfOrganization.getEmail().get());
      message.setSubject(subject);
      message.setText(content);
      message.setFrom(from);
      emailSender.send(message);
    });
  }

  private List<Person> getHeadsOfOrganization(OrganizationUnit unit) {
    return unit.getPersonInRoles().stream()
      .filter(personInRole ->
        personInRole.getRole().getLabel() == RoleLabel.HEAD_OF_ORGANIZATION
      )
      .map(OrganizationPersonInRole::getPerson)
      .filter(person -> person.getEmail().isPresent() && ! person.getEmail().get().isEmpty())
      .collect(Collectors.toList());
  }
}
