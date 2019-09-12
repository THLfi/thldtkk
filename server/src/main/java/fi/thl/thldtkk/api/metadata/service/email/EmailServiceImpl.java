package fi.thl.thldtkk.api.metadata.service.email;

import fi.thl.thldtkk.api.metadata.domain.*;
import fi.thl.thldtkk.api.metadata.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmailServiceImpl implements EmailService {

  public Optional<JavaMailSender> emailSender;

  @Value("${spring.mail.from: info@aineistokatalogi.fi}")
  private String from;

  private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

  public EmailServiceImpl(Optional<JavaMailSender> emailSender) {
    this.emailSender = emailSender;
  }

  public void sendEmail(Person recipient, SimpleMailMessage message) {
    if (!recipient.getEmail().isPresent()) {
      LOG.warn(String.format("Attempted to send email to person without email address: %1$s %2$s",
         recipient.getFirstName().orElse(""), recipient.getLastName().orElse("")));
      return;
    }

    if (!emailSender.isPresent()) {
      LOG.warn("Unable to send email: Email server is not configured");
      return;
    }

    try {
      new InternetAddress(recipient.getEmail().get());
    } catch (AddressException ex) {
      LOG.warn(String.format("Attempted to send email to person with invalid email address: %1$s %2$s",
         recipient.getFirstName().orElse(""), recipient.getLastName().orElse("")));
      return;
    }

    message.setTo(recipient.getEmail().get());
    message.setFrom(from);

    try {
      emailSender.get().send(message);
    } catch (MailSendException ex) {
      LOG.error("Unable to send email: " + ex.getMessage());
    }
  }

  public void sendEmails(List<Person> recipients, SimpleMailMessage message) {
    recipients.forEach(recipient -> sendEmail(recipient, message));
  }
}
