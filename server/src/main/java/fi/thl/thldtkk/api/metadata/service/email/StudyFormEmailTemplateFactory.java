package fi.thl.thldtkk.api.metadata.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import fi.thl.thldtkk.api.metadata.domain.OrganizationUnit;
import fi.thl.thldtkk.api.metadata.domain.Study;

@Component
public class StudyFormEmailTemplateFactory {
  private static final String EDITOR_URL_TEMPLATE = "%1$s/editor/studies/%2$s/edit-administrative-information";

  @Value("${host.url}")
  private String hostUrl;

  public final SimpleMailMessage makeUnitInChargeConfirmationMessage(Study study, OrganizationUnit unit) {
    String template =
      "Sinun yksikkösi on merkitty vastuuseen aineiston %1$s olomuodosta. Käy hyväksymässä vastuutus: %2$s";
    String content =
      String.format(template, study.getPrefLabel().get("fi"), generateEditorLink(study));

    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(content);
    message.setSubject("Aineiston vastuutus");

    return message;
  }

  public final SimpleMailMessage makeRetentionPeriodConfirmationMessage(Study study) {
    String template =
      "Aineiston %1$s säilytysaikaa on muutettu, käy hyväksymässä ehdotettu säilytysaika osoitteessa: %2$s";
    String content =
      String.format(template, study.getPrefLabel().get("fi"), generateEditorLink(study));

    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(content);
    message.setSubject("Aineiston säilytysajan hyväksyminen");

    return message;
  }

  public final SimpleMailMessage makeRetentionPeriodExpirationMessage(Study study) {
    String template =
      "Aineiston %1$s olomuodon säilytysaika on umpeutunut. Tarkista näytteiden säilytystarve ja tarvittaessa päivitä säilytysaika uuteen osoitteessa: %2$s";
    String content =
      String.format(template, study.getPrefLabel().get("fi"), generateEditorLink(study));

    SimpleMailMessage message = new SimpleMailMessage();
    message.setText(content);
    message.setSubject("Aineiston säilytysajan umpeutuminen");

    return message;
  }

  private final String generateEditorLink(Study study) {
    return String.format(EDITOR_URL_TEMPLATE, hostUrl, study.getId());
  }
}
