package fi.thl.thldtkk.api.metadata.service.csv;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ParsingResult<T> {
  private T parsedObject;
  private List<String> messages;

  public ParsingResult(T parsedObject, List<String> messages) {
    this.parsedObject = parsedObject;
    this.messages = messages;
  }

  public Optional<T> getParsedObject() {
    return Optional.ofNullable(parsedObject);
  }

  public List<String> getMessages() {
    return messages != null ? messages : Collections.emptyList();
  }
}
