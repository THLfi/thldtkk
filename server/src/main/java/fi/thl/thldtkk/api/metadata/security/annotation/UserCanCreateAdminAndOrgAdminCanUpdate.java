package fi.thl.thldtkk.api.metadata.security.annotation;

import fi.thl.thldtkk.api.metadata.security.UserRoles;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Entity parameter of the target method must be named <code>entity</code> or denoted with <code>@P("entity")</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("#entity.id == null ? hasAnyAuthority('" + UserRoles.USER + "," + UserRoles.ADMIN + "," + UserRoles.ORG_ADMIN + "') : hasAnyAuthority('" + UserRoles.ADMIN + "," + UserRoles.ORG_ADMIN + "')")
public @interface UserCanCreateAdminAndOrgAdminCanUpdate {
}
