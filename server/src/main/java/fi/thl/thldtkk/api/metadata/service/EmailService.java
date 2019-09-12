package fi.thl.thldtkk.api.metadata.service;

import java.util.List;

import org.springframework.mail.SimpleMailMessage;

import fi.thl.thldtkk.api.metadata.domain.Person;

public interface EmailService {
  void sendEmail(Person recipient, SimpleMailMessage message);
  void sendEmails(List<Person> recipient, SimpleMailMessage message);
}
