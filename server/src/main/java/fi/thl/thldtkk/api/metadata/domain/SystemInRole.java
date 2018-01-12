package fi.thl.thldtkk.api.metadata.domain;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.thl.thldtkk.api.metadata.domain.termed.Node;
import fi.thl.thldtkk.api.metadata.domain.termed.StrictLangValue;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class SystemInRole implements NodeEntity {

  private static final String TERMED_NODE_CLASS = "SystemInRole";

  private UUID id;
  @NotNull
  private System system;
  private SystemRole role;

  public SystemInRole(UUID id) {
    this.id = requireNonNull(id);
  }

  public SystemInRole(UUID id, System system, SystemRole role) {
    this(id);
    this.system = system;
    this.role = role;
  }

  public SystemInRole(Node node) {
    this(node.getId());
    checkArgument(Objects.equals(node.getTypeId(), TERMED_NODE_CLASS));
    node.getReferencesFirst("system").ifPresent(p -> this.system = new System(p));
    node.getReferencesFirst("systemRole").ifPresent(p -> this.role = new SystemRole(p));
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Optional<System> getSystem() {
    return Optional.ofNullable(this.system);
  }

  public Optional<SystemRole> getSystemRole() {
    return Optional.ofNullable(this.role);
  }

  public Node toNode() {
    Multimap<String, StrictLangValue> props = LinkedHashMultimap.create();

    Multimap<String, Node> refs = LinkedHashMultimap.create();
    getSystem().ifPresent(s -> refs.put("system", s.toNode()));
    getSystemRole().ifPresent(sr -> refs.put("systemRole", sr.toNode()));

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
    SystemInRole that = (SystemInRole) o;
    return Objects.equals(id, that.id)
      && Objects.equals(system, that.system)
      && Objects.equals(role, that.role);
  }

  @Override
   public int hashCode() {
    return Objects.hash(id, system, role);
  }

}
