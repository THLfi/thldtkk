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

public class StudyForm implements NodeEntity {

  public static final String TERMED_NODE_CLASS = "StudyForm";

  private UUID id;
  private StudyFormType type;
  private OrganizationUnit unitInCharge;

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

    node.getReferencesFirst("unitInCharge")
      .ifPresent(unit -> this.unitInCharge = new OrganizationUnit(unit));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<OrganizationUnit> getUnitInCharge() {
    return Optional.ofNullable(unitInCharge);
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();
    props.put("type", PropertyMappings.enumToPropertyValue(type));

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
      && Objects.equals(unitInCharge, study.unitInCharge);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      id,
      type,
      unitInCharge
    );
  }

}
