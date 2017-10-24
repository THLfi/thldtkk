package fi.thl.thldtkk.api.metadata.test;

import fi.thl.thldtkk.api.metadata.domain.CodeItem;
import fi.thl.thldtkk.api.metadata.domain.CodeList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fi.thl.thldtkk.api.metadata.util.UUIDs.nameUUIDFromString;

public class CodeListBuilder {

  private UUID id;
  private String codeListType;
  private String referenceId;
  private Map<String, String> prefLabel = new LinkedHashMap<>();
  private Map<String, String> description = new LinkedHashMap<>();
  private Map<String, String> owner = new LinkedHashMap<>();
  private List<CodeItem> codeItems = new ArrayList<>();

  public CodeListBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public CodeListBuilder withIdFromString(String string) {
    return withId(nameUUIDFromString(string));
  }

  public CodeListBuilder withPrefLabel(Map<String, String> prefLabel) {
    this.prefLabel = prefLabel;
    return this;
  }

  public CodeListBuilder withPrefLabel(String prefLabelValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", prefLabelValue);
    return withPrefLabel(langValues);
  }

  public CodeListBuilder withOwner(Map<String, String> owner) {
    this.owner = owner;
    return this;
  }

  public CodeListBuilder withOwner(String ownerValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", ownerValue);
    return withOwner(langValues);
  }

  public CodeListBuilder withDescription(Map<String, String> description) {
    this.description = description;
    return this;
  }

  public CodeListBuilder withDescription(String descriptionValue) {
    Map<String, String> langValues = new LinkedHashMap<>();
    langValues.put("fi", descriptionValue);
    return withDescription(langValues);
  }

  public CodeListBuilder withCodeItems(List<CodeItem> codeItems) {
    this.codeItems = codeItems;
    return this;
  }
  
  public CodeListBuilder withCodeListType(String codeListType) {
    this.codeListType = codeListType;
    return this;
  }
  
  public CodeListBuilder withReferenceId(String referenceId) {
    this.referenceId = referenceId;
    return this;
  }

  public CodeList build() {
    return new CodeList(id, codeListType, referenceId, prefLabel, description, owner, codeItems);
  }

}
