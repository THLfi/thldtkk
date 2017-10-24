package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.InstanceQuestion;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class InstanceQuestionBuilder {
  
  private UUID id;
  private Map<String, String> prefLabel = new LinkedHashMap<>();

  public InstanceQuestionBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public InstanceQuestionBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public InstanceQuestionBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public InstanceQuestionBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }
  
  public InstanceQuestion build() {
    return new InstanceQuestion(id, prefLabel);
  }
  
}
