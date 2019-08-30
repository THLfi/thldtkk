package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.valueToEnum;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLangValueMap;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValues;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toLocalDate;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toPropertyValue;
import static fi.thl.thldtkk.api.metadata.domain.termed.PropertyMappings.toBoolean;
import static java.util.Objects.requireNonNull;

public class StudyForm implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "StudyForm";

  private UUID id;
  private StudyFormType type;
  private StudyFormTypeSpecifier typeSpecifier;
  private Map<String, String> additionalDetails = new LinkedHashMap<>();
  private LocalDate retentionPeriod;
  private LocalDate disposalDate;
  private OrganizationUnit unitInCharge;
  private StudyFormConfirmationState unitInChargeConfirmationState;
  private StudyFormConfirmationState retentionPeriodConfirmationState;

  /**
   * Required by GSON deserialization.
   */
  private StudyForm() {}

  public StudyForm(UUID id) {
    this.id = requireNonNull(id);
  }

  public StudyForm(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));

    this.type = valueToEnum(node.getProperties("type"), StudyFormType.class);
    this.typeSpecifier = valueToEnum(node.getProperties("typeSpecifier"), StudyFormTypeSpecifier.class);
    this.additionalDetails = toLangValueMap(node.getProperties("additionalDetails"));
    this.retentionPeriod = toLocalDate(node.getProperties("retentionPeriod"), null);
    this.disposalDate = toLocalDate(node.getProperties("disposalDate"), null);
    this.unitInChargeConfirmationState = valueToEnum(node.getProperties("unitInChargeConfirmationState"), StudyFormConfirmationState.class);
    this.retentionPeriodConfirmationState = valueToEnum(node.getProperties("retentionPeriodConfirmationState"), StudyFormConfirmationState.class);

    node.getReferencesFirst("unitInCharge")
      .ifPresent(unit -> this.unitInCharge = new OrganizationUnit(unit));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setType(StudyFormType type) {
    this.type = type;
  }

  public StudyFormType getType() {
    return type;
  }

  public void setTypeSpecifier(StudyFormTypeSpecifier typeSpecifier) {
    this.typeSpecifier = typeSpecifier;
  }

  public Map<String, String> getAdditionalDetails() {
    return additionalDetails;
  }

  public Optional<OrganizationUnit> getUnitInCharge() {
    return Optional.ofNullable(unitInCharge);
  }

  public Optional<LocalDate> getRetentionPeriod() {
    return Optional.ofNullable(retentionPeriod);
  }

  public Optional<LocalDate> getDisposalDate() {
    return Optional.ofNullable(disposalDate);
  }

  public Optional<StudyFormConfirmationState> getUnitInChargeConfirmationState() {
    return Optional.ofNullable(unitInChargeConfirmationState);
  }

  public void setUnitInChargeConfirmationState(StudyFormConfirmationState unitInChargeConfirmationState) {
    this.unitInChargeConfirmationState = unitInChargeConfirmationState;
  }

  public Optional<StudyFormConfirmationState> getRetentionPeriodConfirmationState() {
    return Optional.ofNullable(retentionPeriodConfirmationState);
  }

  public void setRetentionPeriodConfirmationState(StudyFormConfirmationState retentionPeriodConfirmationState) {
    this.retentionPeriodConfirmationState = retentionPeriodConfirmationState;
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.put("type", PropertyMappings.enumToPropertyValue(type));
    props.put("typeSpecifier", PropertyMappings.enumToPropertyValue(typeSpecifier));
    props.putAll("additionalDetails", toPropertyValues(additionalDetails));
    getRetentionPeriod().ifPresent(v -> props.put("retentionPeriod", toPropertyValue(v)));
    getDisposalDate().ifPresent(v -> props.put("disposalDate", toPropertyValue(v)));

    getUnitInChargeConfirmationState().ifPresent(v -> props.put("unitInChargeConfirmationState", PropertyMappings.enumToPropertyValue(v)));
    getRetentionPeriodConfirmationState().ifPresent(v -> props.put("retentionPeriodConfirmationState", PropertyMappings.enumToPropertyValue(v)));

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getUnitInCharge().ifPresent(unit -> refs.put("unitInCharge", unit.toNode()));

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
    StudyForm study = (StudyForm) o;
    return Objects.equals(id, study.id)
      && Objects.equals(type, study.type)
      && Objects.equals(typeSpecifier, study.typeSpecifier)
      && Objects.equals(additionalDetails, study.additionalDetails)
      && Objects.equals(retentionPeriod, study.retentionPeriod)
      && Objects.equals(disposalDate, study.disposalDate)
      && Objects.equals(unitInChargeConfirmationState, study.unitInChargeConfirmationState)
      && Objects.equals(retentionPeriodConfirmationState, study.retentionPeriodConfirmationState)
      && Objects.equals(unitInCharge, study.unitInCharge);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      type,
      typeSpecifier,
      additionalDetails,
      retentionPeriod,
      disposalDate,
      unitInChargeConfirmationState,
      retentionPeriodConfirmationState,
      unitInCharge
    );
  }

}
